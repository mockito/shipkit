package org.shipkit.internal.gradle.release;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.exec.ShipkitExecTask;
import org.shipkit.internal.gradle.ReleaseNotesPlugin;
import org.shipkit.internal.gradle.VersioningPlugin;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.gradle.util.TaskSuccessfulMessage;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.ReleaseNotesPlugin.UPDATE_NOTES_TASK;
import static org.shipkit.internal.gradle.exec.ExecCommandFactory.execCommand;
import static org.shipkit.internal.gradle.release.ReleaseNeededPlugin.RELEASE_NEEDED;

/**
 * Release automation: notes generation, tagging, versioning.
 * <p>
 * Applies:
 *
 * <ul>
 *     <li>{@link ReleaseNotesPlugin}</li>
 *     <li>{@link VersioningPlugin}</li>
 *     <li>{@link GitPlugin}</li>
 * </ul>
 *
 * Adds tasks:
 * <ul>
 *     <li>performRelease</li>
 *     <li>testRelease</li>
 *     <li>releaseCleanUp</li>
 * </ul>
 */
public class ReleasePlugin implements Plugin<Project> {

    public static final String PERFORM_RELEASE_TASK = "performRelease";
    public static final String TEST_RELEASE_TASK = "testRelease";
    public static final String RELEASE_CLEAN_UP_TASK = "releaseCleanUp";

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(ReleaseNotesPlugin.class);
        project.getPlugins().apply(GitPlugin.class);
        project.getPlugins().apply(ReleaseNeededPlugin.class);

        TaskMaker.task(project, PERFORM_RELEASE_TASK, new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Performs release. " +
                        "For testing, use: './gradlew testRelease'");

                t.dependsOn(VersioningPlugin.BUMP_VERSION_FILE_TASK, UPDATE_NOTES_TASK);
                t.dependsOn(GitPlugin.PERFORM_GIT_PUSH_TASK);
            }
        });

        TaskMaker.task(project, TEST_RELEASE_TASK, ShipkitExecTask.class, new Action<ShipkitExecTask>() {
            public void execute(ShipkitExecTask t) {
                t.setDescription("Tests the release procedure and cleans up. Safe to be invoked multiple times.");
                //releaseCleanUp is already set up to run all his "subtasks" after performRelease is performed
                //releaseNeeded is used here only to execute the code paths in the release needed task (extra testing)
                t.getExecCommands().add(execCommand("Performing relase in dry run, with cleanup"
                        , asList("./gradlew", RELEASE_NEEDED, PERFORM_RELEASE_TASK, RELEASE_CLEAN_UP_TASK, "-PdryRun")));
                TaskSuccessfulMessage.logOnSuccess(t, "  The release test was successful. Ship it!");
            }
        });

        TaskMaker.task(project, RELEASE_CLEAN_UP_TASK, new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Cleans up the working copy, useful after dry running the release");

                //using finalizedBy so that all clean up tasks run, even if one of them fails
                t.finalizedBy(GitPlugin.PERFORM_GIT_COMMIT_CLEANUP_TASK);
                t.finalizedBy(GitPlugin.TAG_CLEANUP_TASK);
            }
        });
    }
}

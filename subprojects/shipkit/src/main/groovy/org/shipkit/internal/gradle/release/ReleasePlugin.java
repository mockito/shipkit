package org.shipkit.internal.gradle.release;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.exec.ShipkitExecTask;
import org.shipkit.gradle.git.IdentifyGitBranchTask;
import org.shipkit.gradle.notes.UpdateReleaseNotesTask;
import org.shipkit.internal.gradle.git.GitBranchPlugin;
import org.shipkit.internal.gradle.notes.ReleaseNotesPlugin;
import org.shipkit.internal.gradle.notes.tasks.UpdateReleaseNotes;
import org.shipkit.internal.gradle.version.VersioningPlugin;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.gradle.util.TaskSuccessfulMessage;
import org.shipkit.internal.notes.util.Supplier;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.git.GitBranchPlugin.IDENTIFY_GIT_BRANCH;
import static org.shipkit.internal.gradle.notes.ReleaseNotesPlugin.UPDATE_NOTES_TASK;
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
 *     <li>{@link GitBranchPlugin}</li>
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
    public void apply(final Project project) {
        project.getPlugins().apply(ReleaseNotesPlugin.class);
        project.getPlugins().apply(GitPlugin.class);
        project.getPlugins().apply(ReleaseNeededPlugin.class);
        project.getPlugins().apply(GitBranchPlugin.class);

        TaskMaker.task(project, PERFORM_RELEASE_TASK, new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Performs release. " +
                        "For testing, use: './gradlew testRelease'");

                t.dependsOn(VersioningPlugin.BUMP_VERSION_FILE_TASK, UPDATE_NOTES_TASK);
                t.dependsOn(GitPlugin.PERFORM_GIT_PUSH_TASK);
                t.dependsOn(IDENTIFY_GIT_BRANCH);

                final UpdateReleaseNotesTask updateReleaseNotesTask = (UpdateReleaseNotesTask) project.getTasks().getByName(UPDATE_NOTES_TASK);
                final IdentifyGitBranchTask identifyGitBranchTask = (IdentifyGitBranchTask) project.getTasks().getByName(IDENTIFY_GIT_BRANCH);

                TaskSuccessfulMessage.logOnSuccess(t, new Supplier<String>() {
                    @Override
                    public String get() {
                        return "\n" +
                            "Release shipped!\n" +
                            "    - Release notes:      " + new UpdateReleaseNotes().getReleaseNotesUrl(updateReleaseNotesTask, identifyGitBranchTask.getBranch());
                    }
                });
            }
        });

        TaskMaker.task(project, TEST_RELEASE_TASK, ShipkitExecTask.class, new Action<ShipkitExecTask>() {
            public void execute(ShipkitExecTask t) {
                t.setDescription("Tests the release procedure and cleans up. Safe to be invoked multiple times.");
                //releaseCleanUp is already set up to run all his "subtasks" after performRelease is performed
                //releaseNeeded is used here only to execute the code paths in the release needed task (extra testing)
                t.getExecCommands().add(execCommand("Performing release in dry run, with cleanup",
                    asList("./gradlew", RELEASE_NEEDED, PERFORM_RELEASE_TASK, RELEASE_CLEAN_UP_TASK, "-PdryRun")));
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

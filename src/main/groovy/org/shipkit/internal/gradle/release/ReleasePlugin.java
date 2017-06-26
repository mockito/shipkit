package org.shipkit.internal.gradle.release;

import org.gradle.api.*;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.internal.gradle.GitPlugin;
import org.shipkit.internal.gradle.ReleaseConfigurationPlugin;
import org.shipkit.internal.gradle.ReleaseNotesPlugin;
import org.shipkit.internal.gradle.VersioningPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;

import static org.shipkit.internal.gradle.ReleaseNotesPlugin.UPDATE_NOTES_TASK;
import static org.shipkit.internal.gradle.configuration.LazyConfiguration.lazyConfiguration;

/**
 * Applies plugins:
 * <ul>
 *     <li>{@link ReleaseConfigurationPlugin}</li>
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
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        project.getPlugins().apply(ReleaseNotesPlugin.class);
        project.getPlugins().apply(VersioningPlugin.class);
        project.getPlugins().apply(GitPlugin.class);

        TaskMaker.task(project, PERFORM_RELEASE_TASK, new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Performs release. " +
                        "Ship with: './gradlew performRelease -Pshipkit.dryRun=false'. " +
                        "Test with: './gradlew testRelease'");

                t.dependsOn(VersioningPlugin.BUMP_VERSION_FILE_TASK, UPDATE_NOTES_TASK);
                t.dependsOn(GitPlugin.PERFORM_GIT_PUSH_TASK);
            }
        });

        TaskMaker.task(project, TEST_RELEASE_TASK, new Action<Task>() {
            @Override
            public void execute(final Task t) {
                t.setDescription("Tests the release procedure and cleans up. Safe to be invoked multiple times.");
                //releaseCleanUp is already set up to run all his "subtasks" after performRelease is performed
                //releaseNeeded is used here only to execute the code paths in the release needed task (extra testing)
                t.dependsOn("releaseNeeded", "performRelease", "releaseCleanUp");

                //Ensure that when 'testRelease' is invoked we must be using 'dryRun'
                //This is to avoid unintentional releases during testing
                lazyConfiguration(t, new Runnable() {
                    public void run() {
                        if (!conf.isDryRun()) {
                            throw new GradleException("When '" + t.getName() + "' task is executed" +
                                    " 'shipkit.dryRun' must be set to 'true'.\n" +
                                    "See Javadoc for ReleaseConfigurationPlugin.");
                        }
                    }
                });
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

package org.shipkit.internal.gradle.release;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.exec.ExecCommand;
import org.shipkit.gradle.exec.ShipkitExecTask;
import org.shipkit.gradle.git.IdentifyGitBranchTask;
import org.shipkit.gradle.notes.UpdateReleaseNotesTask;
import org.shipkit.internal.gradle.git.GitBranchPlugin;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.javadoc.JavadocPlugin;
import org.shipkit.internal.gradle.notes.ReleaseNotesPlugin;
import org.shipkit.internal.gradle.notes.tasks.UpdateReleaseNotes;
import org.shipkit.internal.gradle.util.GradleWrapper;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.gradle.util.TaskSuccessfulMessage;
import org.shipkit.internal.gradle.version.VersioningPlugin;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.exec.ExecCommandFactory.execCommand;
import static org.shipkit.internal.gradle.git.GitBranchPlugin.IDENTIFY_GIT_BRANCH;
import static org.shipkit.internal.gradle.notes.ReleaseNotesPlugin.UPDATE_NOTES_ON_GITHUB_TASK;
import static org.shipkit.internal.gradle.notes.ReleaseNotesPlugin.UPDATE_NOTES_TASK;
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
 *
 * <ul>
 *     <li>performRelease - ships new release: builds artifacts, generates release notes,
 *       makes version bump commit, creates tag, pushes code, uploads to bintray. Ship it!</li>
 *     <li>testRelease - performs release test in 'dryRun' mode. Useful to test the release logic
 *       without actually performing externally visible commands like 'gitPush' or 'bintrayUpload'.</li>
 *     <li>releaseCleanUp - cleans up after the release (removes version bump commit, removes the tag).
 *       Useful in conjuction with 'testRelease' task.</li>
 *     <li>contributorTestRelease - runs a test of release logic excluding tasks that need secret keys
 *       (git push, bintray upload, etc). See also 'testRelease' task.</li>
 * </ul>
 */
public class ReleasePlugin implements Plugin<Project> {

    public static final String PERFORM_RELEASE_TASK = "performRelease";
    private static final String TEST_RELEASE_TASK = "testRelease";
    public static final String CONTRIBUTOR_TEST_RELEASE_TASK = "contributorTestRelease";
    private static final String RELEASE_CLEAN_UP_TASK = "releaseCleanUp";
    private ShipkitExecTask contributorTestRelease;

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(ReleaseNotesPlugin.class);
        project.getPlugins().apply(GitPlugin.class);
        project.getPlugins().apply(ReleaseNeededPlugin.class);
        project.getPlugins().apply(GitBranchPlugin.class);

        TaskMaker.task(project, PERFORM_RELEASE_TASK, t -> {
            t.setDescription("Performs release. " +
                "For testing, use: './gradlew testRelease'");

            t.dependsOn(VersioningPlugin.BUMP_VERSION_FILE_TASK, UPDATE_NOTES_TASK, UPDATE_NOTES_ON_GITHUB_TASK);
            t.dependsOn(GitPlugin.PERFORM_GIT_PUSH_TASK);
            t.dependsOn(IDENTIFY_GIT_BRANCH);

            final UpdateReleaseNotesTask updateReleaseNotesTask = (UpdateReleaseNotesTask) project.getTasks().getByName(UPDATE_NOTES_TASK);
            final IdentifyGitBranchTask identifyGitBranchTask = (IdentifyGitBranchTask) project.getTasks().getByName(IDENTIFY_GIT_BRANCH);

            TaskSuccessfulMessage.logOnSuccess(t, () -> "\n" +
                "Release shipped!\n" +
                "    - Publication repository: " + updateReleaseNotesTask.getPublicationRepository() + "\n" +
                "    - Release notes:          " + new UpdateReleaseNotes().getReleaseNotesUrl(updateReleaseNotesTask, identifyGitBranchTask.getBranch()));
        });

        TaskMaker.task(project, TEST_RELEASE_TASK, ShipkitExecTask.class, t -> {
            t.setDescription("Tests the release procedure and cleans up. Safe to be invoked multiple times.");
            //releaseCleanUp is already set up to run all his "subtasks" after performRelease is performed
            //releaseNeeded is used here only to execute the code paths in the release needed task (extra testing)
            t.getExecCommands().add(execCommand("Performing release in dry run, with cleanup",
                asList(GradleWrapper.getWrapperCommand(), RELEASE_NEEDED, PERFORM_RELEASE_TASK, RELEASE_CLEAN_UP_TASK, "-PdryRun")));
            TaskSuccessfulMessage.logOnSuccess(t, "  The release test was successful. Ship it!");
        });

        TaskMaker.task(project, RELEASE_CLEAN_UP_TASK, t -> {
            t.setDescription("Cleans up the working copy, useful after dry running the release");

            //using finalizedBy so that all clean up tasks run, even if one of them fails
            t.finalizedBy(GitPlugin.PERFORM_GIT_COMMIT_CLEANUP_TASK);
            t.finalizedBy(GitPlugin.TAG_CLEANUP_TASK);
            t.finalizedBy(ReleaseNotesPlugin.UPDATE_NOTES_ON_GITHUB_CLEANUP_TASK);
        });

        contributorTestRelease = TaskMaker.task(project, CONTRIBUTOR_TEST_RELEASE_TASK, ShipkitExecTask.class, t -> {
            //modelled after testReleaseTask, see its code comments
            t.setDescription("Similar to 'testRelease' but excludes tasks that require Git/Bintray permissions. " +
                "Useful for contributors who don't have the permissions.");

            t.getExecCommands().add(contributorTestCommand());
            TaskSuccessfulMessage.logOnSuccess(t, "  The release test was successful. Ship it!");
        });
    }

    private static ExecCommand contributorTestCommand(String... additionalArguments) {
        List<String> commandLine = new LinkedList<>(asList(
            GradleWrapper.getWrapperCommand(), RELEASE_NEEDED, PERFORM_RELEASE_TASK, RELEASE_CLEAN_UP_TASK, "-PdryRun",
            "-x", GitPlugin.GIT_PUSH_TASK, "-x", ReleaseNotesPlugin.UPDATE_NOTES_ON_GITHUB_TASK,
            "-x", ReleaseNotesPlugin.UPDATE_NOTES_ON_GITHUB_CLEANUP_TASK, "-x", JavadocPlugin.PUSH_JAVADOC_TASK));
        commandLine.addAll(asList(additionalArguments));
        return execCommand("Performing release in dry run, with cleanup", commandLine);
    }

    /**
     * Overwrites the contributorTestRelease task to exclude additional task.
     * Needed so that contributors can test release logic without running tasks that require secret keys.
     */
    public void excludeFromContributorTest(String taskName) {
        contributorTestRelease.setExecCommands(asList(contributorTestCommand("-x", taskName)));
    }
}

package org.shipkit.internal.gradle.notes;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.notes.FetchReleaseNotesTask;
import org.shipkit.gradle.notes.UpdateReleaseNotesTask;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.contributors.github.GitHubContributorsPlugin;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.gradle.version.VersioningPlugin;
import org.shipkit.version.VersionInfo;

import java.io.File;

import static java.util.Collections.singletonList;

/**
 * Adds and configures tasks for generating release notes.
 * <p>
 * Applies plugins:
 * <ul>
 * <li>{@link ShipkitConfigurationPlugin}</li>
 * <li>{@link VersioningPlugin}</li>
 * </ul>
 * <p>
 * The plugin adds following tasks:
 * <p>
 * <ul>
 * <li>fetchReleaseNotes - fetches release notes data, see {@link FetchReleaseNotesTask}</li>
 * <li>updateReleaseNotes - updates release notes file in place, or only displays preview if project property 'preview' exists, see {@link UpdateReleaseNotesTask}</li>
 * </ul>
 * <p>
 * It also adds updates release notes changes if {@link GitPlugin} applied
 */
public class ReleaseNotesPlugin implements Plugin<Project> {

    public static final String PREVIEW_PROJECT_PROPERTY = "preview";
    private static final String FETCH_NOTES_TASK = "fetchReleaseNotes";
    public static final String UPDATE_NOTES_TASK = "updateReleaseNotes";

    public void apply(final Project project) {
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();
        project.getPlugins().apply(VersioningPlugin.class);
        project.getPlugins().apply(GitHubContributorsPlugin.class);

        releaseNotesTasks(project, conf);
    }

    private static void releaseNotesTasks(final Project project, final ShipkitConfiguration conf) {
        final FetchReleaseNotesTask releaseNotesFetcher = TaskMaker.task(project, FETCH_NOTES_TASK, FetchReleaseNotesTask.class, new Action<FetchReleaseNotesTask>() {
            public void execute(final FetchReleaseNotesTask t) {
                t.setDescription("Fetches release notes data from Git and GitHub and serializes them to a file");
                t.setOutputFile(new File(project.getBuildDir(), "detailed-release-notes.ser"));
                t.setGitHubApiUrl(conf.getGitHub().getApiUrl());
                t.setGitHubReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
                t.setGitHubRepository(conf.getGitHub().getRepository());
                t.setPreviousVersion(conf.getPreviousReleaseVersion());
                t.setIgnoreCommitsContaining(conf.getReleaseNotes().getIgnoreCommitsContaining());
            }
        });

        final Task contributorsFetcher = project.getTasks().getByName(GitHubContributorsPlugin.FETCH_CONTRIBUTORS);

        TaskMaker.task(project, UPDATE_NOTES_TASK, UpdateReleaseNotesTask.class, new Action<UpdateReleaseNotesTask>() {
            public void execute(final UpdateReleaseNotesTask t) {
                t.setDescription("Updates release notes file. Run with '-Ppreview' if you only want to see the preview.");

                configureDetailedNotes(t, releaseNotesFetcher, project, conf, contributorsFetcher);

                boolean previewMode = project.hasProperty(PREVIEW_PROJECT_PROPERTY);
                t.setPreviewMode(previewMode);

                if (!previewMode) {
                    File releaseNotesFile = project.file(conf.getReleaseNotes().getFile());
                    GitPlugin.registerChangesForCommitIfApplied(
                        singletonList(releaseNotesFile), "release notes updated", t);
                    t.getOutputs().file(releaseNotesFile);
                }
            }
        });
    }

    private static void configureDetailedNotes(final UpdateReleaseNotesTask task,
                                               final FetchReleaseNotesTask releaseNotesFetcher,
                                               final Project project,
                                               final ShipkitConfiguration conf,
                                               final Task contributorsFetcher) {
        task.dependsOn(releaseNotesFetcher);
        task.dependsOn(contributorsFetcher);

        task.setVersion(project.getVersion().toString());
        task.setTagPrefix(conf.getGit().getTagPrefix());

        task.setDevelopers(conf.getTeam().getDevelopers());
        task.setContributors(conf.getTeam().getContributors());
        task.setGitHubLabelMapping(conf.getReleaseNotes().getLabelMapping());
        task.setReleaseNotesFile(project.file(conf.getReleaseNotes().getFile()));
        task.setGitHubUrl(conf.getGitHub().getUrl());
        task.setGitHubRepository(conf.getGitHub().getRepository());
        task.setPreviousVersion(project.getExtensions().getByType(VersionInfo.class).getPreviousVersion());

        task.setReleaseNotesData(releaseNotesFetcher.getOutputFile());
        task.setContributorsDataFile(contributorsFetcher.getOutputs().getFiles().getSingleFile());
    }
}

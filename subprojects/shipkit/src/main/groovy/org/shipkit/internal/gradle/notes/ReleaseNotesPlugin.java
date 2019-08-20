package org.shipkit.internal.gradle.notes;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.notes.*;
import org.shipkit.internal.gradle.configuration.LazyConfiguration;
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
 * <li>{@link GitHubContributorsPlugin}</li>
 * </ul>
 * <p>
 * The plugin adds following tasks:
 * <p>
 * <ul>
 * <li>fetchReleaseNotes - fetches release notes data, see {@link FetchReleaseNotesTask}</li>
 * <li>updateReleaseNotes - updates release notes file in place, or only displays preview if project property 'preview' exists, see {@link UpdateReleaseNotesTask}</li>
 * <li>updateReleaseNotesOnGitHub - updates release notes on GitHub, or only displays preview if project property 'preview' exists, see {@link org.shipkit.gradle.notes.UpdateReleaseNotesOnGitHubTask}</li>
 * </ul>
 * <p>
 * It also adds updates release notes changes if {@link GitPlugin} applied
 */
public class ReleaseNotesPlugin implements Plugin<Project> {

    private static final String PREVIEW_PROJECT_PROPERTY = "preview";
    private static final String FETCH_NOTES_TASK = "fetchReleaseNotes";
    public static final String UPDATE_NOTES_TASK = "updateReleaseNotes";
    public static final String UPDATE_NOTES_ON_GITHUB_TASK = "updateReleaseNotesOnGitHub";
    public static final String UPDATE_NOTES_ON_GITHUB_CLEANUP_TASK = "updateReleaseNotesOnGitHubCleanUp";

    public void apply(final Project project) {
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();
        project.getPlugins().apply(VersioningPlugin.class);
        project.getPlugins().apply(GitHubContributorsPlugin.class);
        releaseNotesTasks(project, conf);
    }

    private static void releaseNotesTasks(final Project project, final ShipkitConfiguration conf) {
        final FetchReleaseNotesTask releaseNotesFetcher = TaskMaker.task(project, FETCH_NOTES_TASK, FetchReleaseNotesTask.class, task -> {
            task.setDescription("Fetches release notes data from Git and GitHub and serializes them to a file");
            task.setOutputFile(new File(project.getBuildDir(), "detailed-release-notes.ser"));
            task.setGitHubApiUrl(conf.getGitHub().getApiUrl());
            task.setGitHubReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
            task.setGitHubRepository(conf.getGitHub().getRepository());
            task.setPreviousVersion(conf.getPreviousReleaseVersion());
            task.setTagPrefix(conf.getGit().getTagPrefix());
            task.setIgnoreCommitsContaining(conf.getReleaseNotes().getIgnoreCommitsContaining());
            task.setIgnoredContributors(conf.getTeam().getIgnoredContributors());
        });

        Task contributorsFetcher = project.getTasks().getByName(GitHubContributorsPlugin.FETCH_CONTRIBUTORS);

        TaskMaker.task(project, UPDATE_NOTES_TASK, UpdateReleaseNotesTask.class, task -> {
            task.setDescription("Updates release notes file. Run with '-Ppreview' if you only want to see the preview.");

            configureDetailedNotes(task, releaseNotesFetcher, project, conf, contributorsFetcher);

            boolean previewMode = project.hasProperty(PREVIEW_PROJECT_PROPERTY);
            task.setPreviewMode(previewMode);

            if (!previewMode) {
                File releaseNotesFile = project.file(conf.getReleaseNotes().getFile());
                GitPlugin.registerChangesForCommitIfApplied(
                    singletonList(releaseNotesFile), "release notes updated", task);
                task.getOutputs().file(releaseNotesFile);
            }
        });

        UpdateReleaseNotesOnGitHubTask updateReleaseNotesOnGitHubTask = TaskMaker.task(project, UPDATE_NOTES_ON_GITHUB_TASK, UpdateReleaseNotesOnGitHubTask.class, task -> {
            task.setDescription("Updates release notes on GitHub releases page. Run with '-Ppreview' if you only want to see the preview.");
            task.mustRunAfter(GitPlugin.GIT_PUSH_TASK);

            configureDetailedNotes(task, releaseNotesFetcher, project, conf, contributorsFetcher);

            boolean previewMode = project.hasProperty(PREVIEW_PROJECT_PROPERTY);
            task.setPreviewMode(previewMode);

            LazyConfiguration.lazyConfiguration(task, () -> {
                task.setGitHubWriteToken(conf.getGitHub().getWriteAuthToken());
            });
        });

        updateReleaseNotesOnGitHubTask.setGitHubApiUrl(conf.getGitHub().getApiUrl());
        updateReleaseNotesOnGitHubTask.setUpstreamRepositoryName(conf.getGitHub().getRepository());
        updateReleaseNotesOnGitHubTask.setDryRun(conf.isDryRun());

        UpdateReleaseNotesOnGitHubCleanupTask updateReleaseNotesOnGitHubCleanupTask = TaskMaker.task(project, UPDATE_NOTES_ON_GITHUB_CLEANUP_TASK, UpdateReleaseNotesOnGitHubCleanupTask.class, task -> {
            task.setDescription("Remove release notes from GitHub release page created by updateReleaseNotesOnGitHub task.");

            configureDetailedNotes(task, releaseNotesFetcher, project, conf, contributorsFetcher);

            boolean previewMode = project.hasProperty(PREVIEW_PROJECT_PROPERTY);
            task.setPreviewMode(previewMode);

            LazyConfiguration.lazyConfiguration(task, () -> {
                task.setGitHubWriteToken(conf.getGitHub().getWriteAuthToken());
            });
        });

        updateReleaseNotesOnGitHubCleanupTask.setGitHubApiUrl(conf.getGitHub().getApiUrl());
        updateReleaseNotesOnGitHubCleanupTask.setUpstreamRepositoryName(conf.getGitHub().getRepository());
        updateReleaseNotesOnGitHubCleanupTask.setDryRun(conf.isDryRun());
    }

    private static void configureDetailedNotes(AbstractReleaseNotesTask task,
                                               FetchReleaseNotesTask releaseNotesFetcher,
                                               Project project,
                                               ShipkitConfiguration conf,
                                               Task contributorsFetcher) {
        task.dependsOn(releaseNotesFetcher);
        task.dependsOn(contributorsFetcher);

        task.setVersion(project.getVersion().toString());
        task.setTagPrefix(conf.getGit().getTagPrefix());

        task.setGitHubRepository(conf.getGitHub().getRepository());
        task.setDevelopers(conf.getTeam().getDevelopers());

        task.setContributors(conf.getTeam().getContributors());
        if (conf.getTeam().getContributors().isEmpty()) {
            task.setContributorsDataFile(contributorsFetcher.getOutputs().getFiles().getSingleFile());
        }

        task.setGitHubLabelMapping(conf.getReleaseNotes().getLabelMapping());
        task.setReleaseNotesFile(project.file(conf.getReleaseNotes().getFile()));
        task.setGitHubUrl(conf.getGitHub().getUrl());
        task.setPreviousVersion(project.getExtensions().getByType(VersionInfo.class).getPreviousVersion());

        task.setReleaseNotesData(releaseNotesFetcher.getOutputFile());
    }
}

package org.shipkit.internal.gradle.notes.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.notes.UpdateReleaseNotesTask;
import org.shipkit.internal.gradle.util.FileUtil;
import org.shipkit.internal.gradle.util.ReleaseNotesSerializer;
import org.shipkit.internal.notes.contributors.DefaultProjectContributorsSet;
import org.shipkit.internal.notes.contributors.ProjectContributorsSerializer;
import org.shipkit.internal.notes.contributors.ProjectContributorsSet;
import org.shipkit.internal.notes.format.ReleaseNotesFormatters;
import org.shipkit.internal.notes.header.HeaderProvider;
import org.shipkit.internal.notes.model.Contributor;
import org.shipkit.internal.notes.model.ReleaseNotesData;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public class UpdateReleaseNotes {

    private static final Logger LOG = Logging.getLogger(UpdateReleaseNotesTask.class);

    public void updateReleaseNotes(UpdateReleaseNotesTask task, HeaderProvider headerProvider) {
        String newContent = generateNewContent(task, headerProvider);
        updateReleaseNotes(task.isPreviewMode(), task.getReleaseNotesFile(), newContent);
    }

    void updateReleaseNotes(boolean previewMode, File releaseNotesFile, String newContent) {
        if (previewMode) {
            LOG.lifecycle("  Preview of release notes update:\n" +
                "  ----------------\n" + newContent + "----------------");
        } else {
            FileUtil.appendToTop(newContent, releaseNotesFile);
            LOG.lifecycle("  Successfully updated release notes!");
        }
    }

    Map<String, Contributor> contributorsMap(Collection<String> contributorsFromConfiguration,
                                                    ProjectContributorsSet contributorsFromGitHub,
                                                    Collection<String> developers,
                                                    String githubUrl) {
        return new ContributorsMapBuilder(githubUrl)
            .withContributorsFromConfiguration(contributorsFromConfiguration)
            .withContributorsFromGitHub(contributorsFromGitHub)
            .withDevelopers(developers).build();
    }


    public String generateNewContent(UpdateReleaseNotesTask task, HeaderProvider headerProvider) {
        LOG.lifecycle("  Building new release notes based on {}", task.getReleaseNotesFile());

        String headerMessage = headerProvider.getHeader(task.getHeader());

        Collection<ReleaseNotesData> data = new ReleaseNotesSerializer().deserialize(IOUtil.readFully(task.getReleaseNotesData()));

        String vcsCommitTemplate = getVcsCommitTemplate(task);

        ProjectContributorsSet contributorsFromGitHub;
        if (!task.getContributors().isEmpty()) {
            // if contributors are defined in shipkit.team.contributors don't deserialize them from file
            contributorsFromGitHub = new DefaultProjectContributorsSet();
        } else {
            LOG.info("  Read project contributors from file " + task.getContributorsDataFile().getAbsolutePath());
            contributorsFromGitHub = new ProjectContributorsSerializer().deserialize(IOUtil.readFully(task.getContributorsDataFile()));
        }

        Map<String, Contributor> contributorsMap = contributorsMap(task.getContributors(), contributorsFromGitHub, task.getDevelopers(), task.getGitHubUrl());
        String notes = ReleaseNotesFormatters.detailedFormatter(headerMessage,
            "", task.getGitHubLabelMapping(), vcsCommitTemplate, task.getPublicationRepository(), contributorsMap, task.isEmphasizeVersion())
            .formatReleaseNotes(data);

        return notes + "\n\n";
    }

    private String getVcsCommitTemplate(UpdateReleaseNotesTask task) {
        if (task.getPreviousVersion() != null) {
            return task.getGitHubUrl() + "/" + task.getGitHubRepository() + "/compare/"
                + task.getTagPrefix() + task.getPreviousVersion() + "..." + task.getTagPrefix() + task.getVersion();
        } else {
            return "";
        }
    }

    public String getReleaseNotesUrl(UpdateReleaseNotesTask task, String branch) {
        return  task.getGitHubUrl() + "/" + task.getGitHubRepository() + "/blob/" + branch + "/" + task.getProject().relativePath(task.getReleaseNotesFile());
    }
}

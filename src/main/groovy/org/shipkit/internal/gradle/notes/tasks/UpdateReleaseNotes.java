package org.shipkit.internal.gradle.notes.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.notes.UpdateReleaseNotesTask;
import org.shipkit.internal.gradle.util.FileUtil;
import org.shipkit.internal.gradle.util.ReleaseNotesSerializer;
import org.shipkit.internal.gradle.util.team.TeamMember;
import org.shipkit.internal.gradle.util.team.TeamParser;
import org.shipkit.internal.notes.contributors.AllContributorsSerializer;
import org.shipkit.internal.notes.contributors.DefaultContributor;
import org.shipkit.internal.notes.contributors.DefaultProjectContributorsSet;
import org.shipkit.internal.notes.contributors.ProjectContributorsSet;
import org.shipkit.internal.notes.format.ReleaseNotesFormatters;
import org.shipkit.internal.notes.model.Contributor;
import org.shipkit.internal.notes.model.ProjectContributor;
import org.shipkit.internal.notes.model.ReleaseNotesData;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UpdateReleaseNotes {

    private static final Logger LOG = Logging.getLogger(UpdateReleaseNotesTask.class);

    public void updateReleaseNotes(UpdateReleaseNotesTask task) {
        String newContent = generateNewContent(task);
        updateReleaseNotes(task.isPreviewMode(), task.getReleaseNotesFile(), newContent);
    }

    void updateReleaseNotes(boolean previewMode, File releaseNotesFile, String newContent) {
        if (previewMode) {
            LOG.lifecycle("  Preview of release notes update:\n" +
                "  ----------------\n" + newContent + "----------------");
        } else{
            FileUtil.appendToTop(newContent, releaseNotesFile);
            LOG.lifecycle("  Successfully updated release notes!");
        }
    }

    //TODO SF deduplicate and unit test
    static Map<String, Contributor> contributorsMap(Collection<String> contributorsFromConfiguration,
                                                    ProjectContributorsSet contributorsFromGitHub,
                                                    Collection<String> developers) {
        Map<String, Contributor> out = new HashMap<String, Contributor>();
        for (String contributor : contributorsFromConfiguration) {
            TeamMember member = TeamParser.parsePerson(contributor);
            out.put(member.name, new DefaultContributor(member.name, member.gitHubUser,
                "http://github.com/" + member.gitHubUser));
        }
        for (ProjectContributor projectContributor : contributorsFromGitHub.getAllContributors()) {
            out.put(projectContributor.getName(), projectContributor);
        }
        for (String developer : developers) {
            TeamMember member = TeamParser.parsePerson(developer);
            out.put(member.name, new DefaultContributor(member.name, member.gitHubUser,
                "http://github.com/" + member.gitHubUser));
        }
        return out;
    }

    public String generateNewContent(UpdateReleaseNotesTask task) {
        LOG.lifecycle("  Building new release notes based on {}", task.getReleaseNotesFile());

        Collection<ReleaseNotesData> data = new ReleaseNotesSerializer().deserialize(IOUtil.readFully(task.getReleaseNotesData()));

        String vcsCommitTemplate = getVcsCommitTemplate(task);

        ProjectContributorsSet contributorsFromGitHub;
        if (!task.getContributors().isEmpty()) {
            // if contributors are defined in shipkit.team.contributors don't deserialize them from file
            contributorsFromGitHub = new DefaultProjectContributorsSet();
        } else {
            LOG.info("  Read project contributors from file " + task.getContributorsDataFile().getAbsolutePath());
            contributorsFromGitHub = new AllContributorsSerializer().deserialize(IOUtil.readFully(task.getContributorsDataFile()));
        }

        Map<String, Contributor> contributorsMap = contributorsMap(task.getContributors(), contributorsFromGitHub, task.getDevelopers());
        String notes = ReleaseNotesFormatters.detailedFormatter(
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
}

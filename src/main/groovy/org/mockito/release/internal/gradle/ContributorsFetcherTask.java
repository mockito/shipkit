package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.exec.Exec;
import org.mockito.release.exec.ProcessRunner;
import org.mockito.release.notes.contributors.Contributors;
import org.mockito.release.notes.contributors.ContributorsSerializer;
import org.mockito.release.notes.contributors.ContributorsSet;
import org.mockito.release.notes.contributors.GitHubContributorsProvider;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.vcs.ContributionsProvider;
import org.mockito.release.notes.vcs.RevisionProvider;
import org.mockito.release.notes.vcs.Vcs;

import java.io.File;

/**
 * Fetch info about contributors from GitHub and store it in file. It is used later in generation release notes.
 */
public class ContributorsFetcherTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(ContributorsFetcherTask.class);

    @Input private String repository;
    @Input private String authToken;
    @Input private String fromRevision;
    @Input private String toRevision;

    @OutputFile private File contributorsFile;

    @TaskAction
    public void fetchLastContributorsFromGitHub() {
        LOG.lifecycle("  Fetching contributors information between revisions {}..{}", fromRevision, toRevision);
        ProcessRunner processRunner = Exec.getProcessRunner(getProject().getRootDir());
        ContributionsProvider contributionsProvider = Vcs.getContributionsProvider(processRunner);
        ContributionSet contributions = contributionsProvider.getContributionsBetween(fromRevision, toRevision);

        RevisionProvider revisionProvider = Vcs.getRevisionProvider(processRunner);
        String fromRev = revisionProvider.getRevisionForTagOrRevision(fromRevision);

        GitHubContributorsProvider contributorsProvider = Contributors.getGitHubContibutorsProvider(repository, authToken);
        ContributorsSet contributors = contributorsProvider.mapContributorsToGitHubUser(contributions, fromRev, toRevision);

        ContributorsSerializer contributorsSerializer = Contributors.getLastContributorsSerializer(contributorsFile);
        contributorsSerializer.serialize(contributors);
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setToRevision(String toRevision) {
        this.toRevision = toRevision;
    }

    public void setFromRevision(String fromRevision) {
        this.fromRevision = fromRevision;
    }

    public void setContributorsFile(File contributorsFile) {
        this.contributorsFile = contributorsFile;
    }
}

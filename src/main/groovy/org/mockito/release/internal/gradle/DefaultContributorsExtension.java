package org.mockito.release.internal.gradle;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
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


public class DefaultContributorsExtension {

    private static final Logger LOG = Logging.getLogger(DefaultReleaseNotesExtension.class);

    private File workDir;
    private File buildDir;
    private String repository;
    private String authToken;

    void fetchContributorsFromGitHub(String fromRevision, String toRevision) {
        LOG.lifecycle("fetchContributorsFromGitHub from {} to {}", fromRevision, toRevision);
        ProcessRunner processRunner = Exec.getProcessRunner(workDir);
        ContributionsProvider contributionsProvider = Vcs.getContributionsProvider(processRunner);
        ContributionSet contributions = contributionsProvider.getContributionsBetween(fromRevision, toRevision);

        RevisionProvider revisionProvider = Vcs.getRevisionProvider(processRunner);
        String fromRev = revisionProvider.getRevisionForTagOrRevision(fromRevision);

        GitHubContributorsProvider contributorsProvider = Contributors.getGitHubContibutorsProvider(repository, authToken);
        ContributorsSet contributors = contributorsProvider.mapContributorsToGitHubUser(contributions, fromRev, toRevision);

        String contributorsFileName = Contributors.getContributorsFileName(buildDir.getAbsolutePath(), fromRev, toRevision);
        new ContributorsSerializer(contributorsFileName).serialize(contributors);
    }

    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    public void setBuildDir(File buildDir) {
        this.buildDir = buildDir;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}

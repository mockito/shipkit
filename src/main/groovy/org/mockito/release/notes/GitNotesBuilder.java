package org.mockito.release.notes;

import org.mockito.release.exec.Exec;
import org.mockito.release.notes.contributors.Contributors;
import org.mockito.release.notes.contributors.ContributorsMap;
import org.mockito.release.notes.contributors.GitHubContributorsProvider;
import org.mockito.release.notes.format.ReleaseNotesFormatters;
import org.mockito.release.notes.format.SingleReleaseNotesFormatter;
import org.mockito.release.notes.improvements.Improvements;
import org.mockito.release.notes.improvements.ImprovementsProvider;
import org.mockito.release.notes.internal.DefaultReleaseNotesData;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.notes.vcs.ContributionsProvider;
import org.mockito.release.notes.vcs.Vcs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

class GitNotesBuilder implements NotesBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(GitNotesBuilder.class);

    private final File workDir;
    private final String authToken;
    private final String repository;

    /**
     * @param workDir the working directory for external processes execution (for example: git log)
     * @param repository GitHub repository, for example: "mockito/mockito"
     * @param authToken the GitHub auth token
     */
    GitNotesBuilder(File workDir, String repository, String authToken) {
        this.workDir = workDir;
        this.repository = repository;
        this.authToken = authToken;
    }

    public String buildNotes(String version, String fromRevision, String toRevision, final Map<String, String> labels) {
        LOG.info("Getting release notes between {} and {}", fromRevision, toRevision);

        ContributionsProvider contributionsProvider = Vcs.getContributionsProvider(Exec.getProcessRunner(workDir));
        ContributionSet contributions = contributionsProvider.getContributionsBetween(fromRevision, toRevision);

        GitHubContributorsProvider contibutorsProvider = Contributors.getGitHubContibutorsProvider(authToken);
        ContributorsMap contributors = contibutorsProvider.mapContributorsToGitHubUser(contributions, fromRevision, toRevision);

        ImprovementsProvider improvementsProvider = Improvements.getGitHubProvider(repository, authToken);
        Collection<Improvement> improvements = improvementsProvider.getImprovements(contributions, Collections.<String>emptyList(), false);

        ReleaseNotesData data = new DefaultReleaseNotesData(version, new Date(), contributions, improvements, contributors, fromRevision, toRevision);
        SingleReleaseNotesFormatter formatter = ReleaseNotesFormatters.defaultFormatter(labels);

        return formatter.formatVersion(data);
    }
}

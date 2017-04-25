package org.mockito.release.notes;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.mockito.release.exec.Exec;
import org.mockito.release.exec.ProcessRunner;
import org.mockito.release.notes.contributors.Contributors;
import org.mockito.release.notes.contributors.ContributorsLoader;
import org.mockito.release.notes.contributors.ContributorsReader;
import org.mockito.release.notes.contributors.ContributorsSet;
import org.mockito.release.notes.format.ReleaseNotesFormatters;
import org.mockito.release.notes.format.SingleReleaseNotesFormatter;
import org.mockito.release.notes.improvements.Improvements;
import org.mockito.release.notes.improvements.ImprovementsProvider;
import org.mockito.release.notes.internal.DefaultReleaseNotesData;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.notes.vcs.ContributionsProvider;
import org.mockito.release.notes.vcs.RevisionProvider;
import org.mockito.release.notes.vcs.Vcs;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

class GitNotesBuilder implements NotesBuilder {

    private static final Logger LOG = Logging.getLogger(GitNotesBuilder.class);

    private final File workDir;
    private final String authToken;
    private final File buildDir;
    private final String repository;

    /**
     * @param workDir the working directory for external processes execution (for example: git log)
     * @param buildDir build dir
     * @param repository GitHub repository, for example: "mockito/mockito"
     * @param authToken the GitHub auth token
     */
    GitNotesBuilder(File workDir, File buildDir, String repository, String authToken) {
        this.workDir = workDir;
        this.buildDir = buildDir;
        this.repository = repository;
        this.authToken = authToken;
    }

    public String buildNotes(String version, String fromRevision, String toRevision, final Map<String, String> labels,
                             String publicationRepository) {
        LOG.info("Getting release notes between {} and {}", fromRevision, toRevision);

        ProcessRunner processRunner = Exec.getProcessRunner(workDir);
        ContributionsProvider contributionsProvider = Vcs.getContributionsProvider(processRunner);
        ContributionSet contributions = contributionsProvider.getContributionsBetween(fromRevision, toRevision);

        RevisionProvider revisionProvider = Vcs.getRevisionProvider(processRunner);
        String fromRev = revisionProvider.getRevisionForTagOrRevision(fromRevision);

        ContributorsReader contributorsReader = ContributorsLoader.getContributorsReader();
        String contributorsFileName = Contributors.getContributorsFileName(buildDir.getAbsolutePath(), fromRevision, toRevision);
        ContributorsSet contributors = contributorsReader.loadContributors(contributorsFileName, fromRev, toRevision);

        ImprovementsProvider improvementsProvider = Improvements.getGitHubProvider(repository, authToken);
        Collection<Improvement> improvements = improvementsProvider.getImprovements(contributions, Collections.<String>emptyList(), false);

        ReleaseNotesData data = new DefaultReleaseNotesData(version, new Date(), contributions, improvements, contributors, fromRevision, toRevision);
        SingleReleaseNotesFormatter formatter = ReleaseNotesFormatters.defaultFormatter(labels, publicationRepository);

        return formatter.formatVersion(data);
    }
}

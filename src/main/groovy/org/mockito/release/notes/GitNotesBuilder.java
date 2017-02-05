package org.mockito.release.notes;

import org.mockito.release.exec.Exec;
import org.mockito.release.notes.format.DefaultFormatter;
import org.mockito.release.notes.improvements.*;
import org.mockito.release.notes.vcs.ContributionSet;
import org.mockito.release.notes.vcs.ContributionsProvider;
import org.mockito.release.notes.vcs.Vcs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

class GitNotesBuilder implements NotesBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(GitNotesBuilder.class);

    private final File workDir;
    private final String authTokenEnvVar;

    /**
     * @param workDir the working directory for external processes execution (for example: git log)
     * @param authTokenEnvVar the env var that holds the GitHub auth token
     */
    GitNotesBuilder(File workDir, String authTokenEnvVar) {
        this.workDir = workDir;
        this.authTokenEnvVar = authTokenEnvVar;
    }

    public String buildNotes(String version, String fromRevision, String toRevision, Map<String, String> labels) {
        LOG.info("Getting release notes between {} and {}", fromRevision, toRevision);

        ContributionsProvider contributionsProvider = Vcs.getGitProvider(Exec.getProcessRunner(workDir));
        ContributionSet contributions = contributionsProvider.getContributionsBetween(fromRevision, toRevision);

        ImprovementsProvider improvementsProvider = Improvements.getGitHubProvider(authTokenEnvVar);
        Collection<Improvement> improvements = improvementsProvider.getImprovements(contributions, labels);

        return DefaultFormatter.formatNotes(version, new Date(), contributions, labels, improvements);
    }
}

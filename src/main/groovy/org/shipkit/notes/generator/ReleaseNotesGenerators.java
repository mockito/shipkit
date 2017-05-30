package org.shipkit.notes.generator;

import org.shipkit.exec.Exec;
import org.shipkit.exec.ProcessRunner;
import org.shipkit.notes.contributors.Contributors;
import org.shipkit.notes.contributors.GitHubContributorsProvider;
import org.shipkit.notes.improvements.Improvements;
import org.shipkit.notes.improvements.ImprovementsProvider;
import org.shipkit.notes.model.Commit;
import org.shipkit.notes.util.Predicate;
import org.shipkit.notes.vcs.ContributionsProvider;
import org.shipkit.notes.vcs.ReleasedVersionsProvider;
import org.shipkit.notes.vcs.Vcs;

import java.io.File;

public class ReleaseNotesGenerators {

    //TODO move entire "org.mockito.release.notes" -> "org.mockito.release.internal.notes"

    /**
     * @param workDir the working directory where 'git' operations will be executed
     * @param gitHubRepository GitHub gitHubRepository in format USER|COMPANY/REPO_NAME, for example: mockito/mockito
     * @param readOnlyAuthToken read only auth token used to communicate with GitHub
     * @param ignoredCommit responsible decide if commits should not be included in release notes
     */
    public static ReleaseNotesGenerator releaseNotesGenerator(File workDir, String gitHubRepository, String readOnlyAuthToken, Predicate<Commit> ignoredCommit) {
        ProcessRunner processRunner = Exec.getProcessRunner(workDir);
        ContributionsProvider contributionsProvider = Vcs.getContributionsProvider(processRunner, ignoredCommit);
        ImprovementsProvider improvementsProvider = Improvements.getGitHubProvider(gitHubRepository, readOnlyAuthToken);
        ReleasedVersionsProvider releasedVersionsProvider = Vcs.getReleaseDateProvider(processRunner);
        GitHubContributorsProvider contributorsProvider = Contributors.getGitHubContributorsProvider(gitHubRepository, readOnlyAuthToken);
        return new DefaultReleaseNotesGenerator(contributionsProvider, improvementsProvider, releasedVersionsProvider,
                contributorsProvider);
    }
}

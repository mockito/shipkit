package org.mockito.release.notes.generator;

import org.mockito.release.exec.Exec;
import org.mockito.release.exec.ProcessRunner;
import org.mockito.release.notes.contributors.Contributors;
import org.mockito.release.notes.contributors.GitHubContributorsProvider;
import org.mockito.release.notes.improvements.Improvements;
import org.mockito.release.notes.improvements.ImprovementsProvider;
import org.mockito.release.notes.model.Commit;
import org.mockito.release.notes.util.Predicate;
import org.mockito.release.notes.vcs.ContributionsProvider;
import org.mockito.release.notes.vcs.ReleasedVersionsProvider;
import org.mockito.release.notes.vcs.Vcs;

import java.io.File;

public class ReleaseNotesGenerators {

    //TODO move entire "org.mockito.release.notes" -> "org.mockito.release.internal.notes"

    /**
     * @param workDir the working directory where 'git' operations will be executed
     * @param gitHubRepository GitHub gitHubRepository in format USER|COMPANY/REPO_NAME, for example: mockito/mockito
     * @param readOnlyAuthToken read only auth token used to communicate with GitHub
     * @param commitIgnored responsible decide if commits should not be included in release notes
     */
    public static ReleaseNotesGenerator releaseNotesGenerator(File workDir, String gitHubRepository, String readOnlyAuthToken, Predicate<Commit> commitIgnored) {
        ProcessRunner processRunner = Exec.getProcessRunner(workDir);
        ContributionsProvider contributionsProvider = Vcs.getContributionsProvider(processRunner, commitIgnored);
        ImprovementsProvider improvementsProvider = Improvements.getGitHubProvider(gitHubRepository, readOnlyAuthToken);
        ReleasedVersionsProvider releasedVersionsProvider = Vcs.getReleaseDateProvider(processRunner);
        GitHubContributorsProvider contributorsProvider = Contributors.getGitHubContributorsProvider(gitHubRepository, readOnlyAuthToken);
        return new DefaultReleaseNotesGenerator(contributionsProvider, improvementsProvider, releasedVersionsProvider,
                contributorsProvider);
    }
}

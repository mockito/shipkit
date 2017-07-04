package org.shipkit.internal.notes.generator;

import org.shipkit.internal.exec.Exec;
import org.shipkit.internal.exec.ProcessRunner;
import org.shipkit.internal.notes.contributors.github.Contributors;
import org.shipkit.internal.notes.contributors.github.GitHubContributorsProvider;
import org.shipkit.internal.notes.improvements.Improvements;
import org.shipkit.internal.notes.improvements.ImprovementsProvider;
import org.shipkit.internal.notes.model.Commit;
import org.shipkit.internal.notes.util.Predicate;
import org.shipkit.internal.notes.vcs.ContributionsProvider;
import org.shipkit.internal.notes.vcs.ReleasedVersionsProvider;
import org.shipkit.internal.notes.vcs.Vcs;

import java.io.File;

public class ReleaseNotesGenerators {

    /**
     * @param workDir the working directory where 'git' operations will be executed
     * @param gitHubApiUrl GitHub API endpoint address, for example: https://api.github.com
     * @param gitHubRepository GitHub gitHubRepository in format USER|COMPANY/REPO_NAME, for example: mockito/mockito
     * @param readOnlyAuthToken read only auth token used to communicate with GitHub
     * @param ignoredCommit responsible decide if commits should not be included in release notes
     */
    public static ReleaseNotesGenerator releaseNotesGenerator(File workDir, String gitHubApiUrl, String gitHubRepository, String readOnlyAuthToken, Predicate<Commit> ignoredCommit) {
        ProcessRunner processRunner = Exec.getProcessRunner(workDir);
        ContributionsProvider contributionsProvider = Vcs.getContributionsProvider(processRunner, ignoredCommit);
        ImprovementsProvider improvementsProvider = Improvements.getGitHubProvider(gitHubApiUrl, gitHubRepository, readOnlyAuthToken);
        ReleasedVersionsProvider releasedVersionsProvider = Vcs.getReleaseDateProvider(processRunner);
        GitHubContributorsProvider contributorsProvider = Contributors.getGitHubContributorsProvider(gitHubApiUrl, gitHubRepository, readOnlyAuthToken);
        return new DefaultReleaseNotesGenerator(contributionsProvider, improvementsProvider, releasedVersionsProvider,
                contributorsProvider);
    }
}

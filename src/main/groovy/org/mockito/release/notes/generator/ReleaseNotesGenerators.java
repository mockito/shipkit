package org.mockito.release.notes.generator;

import org.mockito.release.exec.Exec;
import org.mockito.release.exec.ProcessRunner;
import org.mockito.release.notes.improvements.Improvements;
import org.mockito.release.notes.improvements.ImprovementsProvider;
import org.mockito.release.notes.vcs.ContributionsProvider;
import org.mockito.release.notes.vcs.ReleaseDateProvider;
import org.mockito.release.notes.vcs.Vcs;

import java.io.File;

public class ReleaseNotesGenerators {

    public static ReleaseNotesGenerator releaseNotesGenerator(File workDir, String repository, String authToken) {
        ProcessRunner processRunner = Exec.getProcessRunner(workDir);
        ContributionsProvider contributionsProvider = Vcs.getContributionsProvider(processRunner);
        ImprovementsProvider improvementsProvider = Improvements.getGitHubProvider(repository, authToken);
        ReleaseDateProvider releaseDateProvider = Vcs.getReleaseDateProvider(processRunner);
        return new DefaultReleaseNotesGenerator(contributionsProvider, improvementsProvider, releaseDateProvider);
    }
}

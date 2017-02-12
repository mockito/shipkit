package org.mockito.release.notes.generator;

import org.mockito.release.exec.Exec;
import org.mockito.release.notes.improvements.Improvements;
import org.mockito.release.notes.improvements.ImprovementsProvider;
import org.mockito.release.notes.internal.DefaultReleaseNotesGenerator;
import org.mockito.release.notes.vcs.ContributionsProvider;
import org.mockito.release.notes.vcs.Vcs;

import java.io.File;

public class ReleaseNotesGenerators {

    public static ReleaseNotesGenerator releaseNotesGenerator(File workDir, String authToken) {
        ContributionsProvider contributionsProvider = Vcs.getGitProvider(Exec.getProcessRunner(workDir));
        ImprovementsProvider improvementsProvider = Improvements.getGitHubProvider(authToken);
        return new DefaultReleaseNotesGenerator(contributionsProvider, improvementsProvider);
    }
}

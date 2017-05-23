package org.mockito.release.notes.vcs;

import org.mockito.release.exec.ProcessRunner;
import org.mockito.release.notes.model.Commit;
import org.mockito.release.notes.util.Predicate;

/**
 * Vcs services
 */
public class Vcs {

    /**
     * Provides means to get contributions.
     */
    public static ContributionsProvider getContributionsProvider(ProcessRunner runner, Predicate<Commit> commitIgnored) {
        return new GitContributionsProvider(new GitLogProvider(runner), commitIgnored);
    }

    /**
     * Provides means to get release versions
     */
    public static ReleasedVersionsProvider getReleaseDateProvider(ProcessRunner runner) {
        return new DefaultReleasedVersionsProvider(runner);
    }

    public static RevisionProvider getRevisionProvider(ProcessRunner runner) {
        return new GitRevisionProvider(runner);
    }
}

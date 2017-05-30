package org.shipkit.notes.vcs;

import org.shipkit.exec.ProcessRunner;
import org.shipkit.notes.model.Commit;
import org.shipkit.notes.util.Predicate;

/**
 * Vcs services
 */
public class Vcs {

    /**
     * Provides means to get contributions.
     */
    public static ContributionsProvider getContributionsProvider(ProcessRunner runner, Predicate<Commit> ignoredCommit) {
        return new GitContributionsProvider(new GitLogProvider(runner), ignoredCommit);
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

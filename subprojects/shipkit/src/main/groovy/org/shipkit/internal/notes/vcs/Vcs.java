package org.shipkit.internal.notes.vcs;

import org.shipkit.internal.exec.ProcessRunner;
import org.shipkit.internal.notes.model.Commit;
import org.shipkit.internal.notes.util.Predicate;

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
}

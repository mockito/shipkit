package org.shipkit.internal.gradle.release.tasks;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.release.ReleaseNeededTask;
import org.shipkit.internal.util.Pair;
import org.shipkit.internal.util.EnvVariables;

public class ReleaseNeeded {

    private final static Logger LOG = Logging.getLogger(ReleaseNeededTask.class);

    //We are using environment variable instead of system property or Gradle project property here
    //It's easier to configure Travis CI matrix builds using env variables
    //For reference, see the ".travis.yml" of Mockito project
    private final static String SKIP_RELEASE_ENV = "SKIP_RELEASE";
    private final static String SKIP_RELEASE_KEYWORD = "[ci skip-release]";
    private static final String FORCE_RELEASE_KEYWORD = "[ci force-release]";

    public boolean releaseNeeded(ReleaseNeededTask task) {
        Pair<Boolean, String> pair = releaseNeeded(task, new EnvVariables());

        boolean releaseNeeded = pair.getLeft();
        String message = pair.getRight();

        if (!releaseNeeded && task.isExplosive()) {
            throw new GradleException(message);
        } else {
            LOG.lifecycle(message);
        }
        return releaseNeeded;
    }

    Pair<Boolean, String> releaseNeeded(ReleaseNeededTask task, EnvVariables envVariables) {
        boolean skipEnvVariable = envVariables.getNonEmptyEnv(SKIP_RELEASE_ENV) != null;
        LOG.lifecycle("  Environment variable {} present: {}", SKIP_RELEASE_ENV, skipEnvVariable);

        boolean commitMessageEmpty = task.getCommitMessage() == null || task.getCommitMessage().trim().isEmpty();
        boolean skippedByCommitMessage = !commitMessageEmpty && task.getCommitMessage().contains(SKIP_RELEASE_KEYWORD);
        boolean forcedByCommitMessage = !commitMessageEmpty && task.getCommitMessage().contains(FORCE_RELEASE_KEYWORD);
        LOG.lifecycle("  Commit message to inspect for keywords '{}' and '{}': {}",
            SKIP_RELEASE_KEYWORD, FORCE_RELEASE_KEYWORD,
            commitMessageEmpty ? "<unknown commit message>" : "\n" + task.getCommitMessage());

        boolean releasableBranch = task.getBranch() != null && task.getBranch().matches(task.getReleasableBranchRegex());
        LOG.lifecycle("  Current branch '{}' matches '{}': {}", task.getBranch(), task.getReleasableBranchRegex(), releasableBranch);

        if (releasableBranch) {
            if (skippedByCommitMessage) {
                return Pair.of(false, " Skipping release due to skip release keyword in commit message.");
            } else if (skipEnvVariable) {
                return Pair.of(false, " Skipping release due to skip release env variable.");
            } else if (task.isPullRequest()) {
                return Pair.of(false, " Skipping release due to is PR.");
            } else if (forcedByCommitMessage) {
                return Pair.of(true, " Releasing due to force release keyword in commit message.");
            } else {
                ComparisonResults results = new ComparisonResults(task.getComparisonResults());
                boolean publicationsIdentical = results.areResultsIdentical();
                LOG.lifecycle(results.getDescription());

                if (publicationsIdentical) {
                    return Pair.of(false, " Skipping release because publications are identical.");
                }
                return Pair.of(true, " Releasing because publication changed.");
            }
        } else {
            return Pair.of(false, " Skipping release because we are not on a releasable branch.");
        }
    }
}

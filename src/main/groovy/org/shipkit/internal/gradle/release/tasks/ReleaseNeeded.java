package org.shipkit.internal.gradle.release.tasks;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.ReleaseNeededTask;
import org.shipkit.internal.notes.util.IOUtil;
import org.shipkit.internal.util.EnvVariables;

import java.io.File;
import java.util.List;

public class ReleaseNeeded {

    private final static Logger LOG = Logging.getLogger(ReleaseNeededTask.class);

    //We are using environment variable instead of system property or Gradle project property here
    //It's easier to configure Travis CI matrix builds using env variables
    //For reference, see the ".travis.yml" of Mockito project
    private final static String SKIP_RELEASE_ENV = "SKIP_RELEASE";
    private final static String SKIP_RELEASE_KEYWORD = "[ci skip-release]";

    public boolean releaseNeeded(ReleaseNeededTask task) {
        return releaseNeeded(task, new EnvVariables());
    }

    public boolean releaseNeeded(ReleaseNeededTask task, EnvVariables envVariables) {
        boolean skipEnvVariable = envVariables.getenv(SKIP_RELEASE_ENV) != null;
        LOG.lifecycle("  Environment variable {} present: {}", SKIP_RELEASE_ENV, skipEnvVariable);

        boolean commitMessageEmpty = task.getCommitMessage() == null || task.getCommitMessage().trim().isEmpty();
        boolean skippedByCommitMessage = !commitMessageEmpty && task.getCommitMessage().contains(SKIP_RELEASE_KEYWORD);
        LOG.lifecycle("  Commit message to inspect for keyword '{}': {}",
                SKIP_RELEASE_KEYWORD,
                commitMessageEmpty? "<unknown commit message>" : "\n" + task.getCommitMessage());

        boolean releasableBranch = task.getBranch() != null && task.getBranch().matches(task.getReleasableBranchRegex());
        LOG.lifecycle("  Current branch '{}' matches '{}': {}", task.getBranch(), task.getReleasableBranchRegex(), releasableBranch);

        boolean publicationsChanged = publicationsChanged(task.getComparisonResults());

        boolean releaseNotNeeded = !publicationsChanged || skipEnvVariable || skippedByCommitMessage || task.isPullRequest() || !releasableBranch;

        String message = "  Release is needed: " + !releaseNotNeeded +
                "\n    - skip by env variable: " + skipEnvVariable +
                "\n    - skip by commit message: " + skippedByCommitMessage +
                "\n    - is pull request build:  " + task.isPullRequest() +
                "\n    - is releasable branch:  " + releasableBranch +
                "\n    - publications changed since previous release:  " + publicationsChanged;

        if (releaseNotNeeded && task.isExplosive()) {
            throw new GradleException(message);
        } else {
            LOG.lifecycle(message);
        }

        return !releaseNotNeeded;
    }

    private static boolean containsDifferences(File comparisonResult) {
        return comparisonResult.isFile() && comparisonResult.length() > 0;
    }

    public static boolean publicationsChanged(List<File> comparisonResults) {
        if (comparisonResults.isEmpty()) {
            return true;
        }

        boolean changed = false;
        LOG.lifecycle("\n  Results of publications comparison:\n");
        for (File result : comparisonResults) {
            if (containsDifferences(result)) {
                LOG.lifecycle(IOUtil.readFully(result));
                changed = true;
            }
        }

        return changed;
    }
}

package org.shipkit.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.notes.util.IOUtil;
import org.shipkit.internal.util.EnvVariables;
import org.shipkit.internal.util.ExposedForTesting;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Decides if the release is needed.
 * It is necessary to avoid making releases in certain scenarios like when we are building pull requests.
 * <p>
 * The release is <strong>not needed</strong> when any of below is true:
 *  - the env variable 'SKIP_RELEASE' is present
 *  - the commit message, loaded from 'TRAVIS_COMMIT_MESSAGE' env variable contains '[ci skip-release]' keyword
 *  - the env variable 'TRAVIS_PULL_REQUEST' is not empty, not an empty String and and not 'false'
 *  - the current Git branch does not match release-eligibility regex ({@link #getReleasableBranchRegex()}.
 *  - binaries have not changes since the previous release
 */
public class ReleaseNeededTask extends DefaultTask {

    private final static Logger LOG = Logging.getLogger(ReleaseNeededTask.class);

    //We are using environment variable instead of system property or Gradle project property here
    //It's easier to configure Travis CI matrix builds using env variables
    //For reference, see the ".travis.yml" of Mockito project
    private final static String SKIP_RELEASE_ENV = "SKIP_RELEASE";
    private final static String SKIP_RELEASE_KEYWORD = "[ci skip-release]";

    private String branch;
    private String releasableBranchRegex;
    private String commitMessage;
    private boolean pullRequest;
    private boolean explosive;
    private EnvVariables envVariables = new EnvVariables();
    private List<File> comparisonResults = new LinkedList<File>();

    /**
     * The branch we currently operate on
     */
    public String getBranch() {
        return branch;
    }

    /**
     * See {@link #getBranch()}
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * Regex to be used to identify branches that are entitled to be released, for example "master|release/.+"
     */
    public String getReleasableBranchRegex() {
        return releasableBranchRegex;
    }

    /**
     * See {@link #getReleasableBranchRegex()}
     */
    public void setReleasableBranchRegex(String releasableBranchRegex) {
        this.releasableBranchRegex = releasableBranchRegex;
    }

    /**
     * Commit message the build job was triggered with
     */
    public String getCommitMessage() {
        return commitMessage;
    }

    /**
     * See {@link #getCommitMessage()}
     */
    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    /**
     * Pull request this job is building
     */
    public boolean isPullRequest() {
        return pullRequest;
    }

    /**
     * See {@link #isPullRequest()}
     */
    public void setPullRequest(boolean pullRequest) {
        this.pullRequest = pullRequest;
    }

    /**
     * If the exception should be thrown if the release is not needed.
     */
    public boolean isExplosive() {
        return explosive;
    }

    /**
     * See {@link #isExplosive()}
     */
    public ReleaseNeededTask setExplosive(boolean explosive) {
        this.explosive = explosive;
        return this;
    }

    @TaskAction public boolean releaseNeeded() {
        boolean skipEnvVariable = envVariables.getenv(SKIP_RELEASE_ENV) != null;
        LOG.lifecycle("  Environment variable {} present: {}", SKIP_RELEASE_ENV, skipEnvVariable);

        boolean commitMessageEmpty = commitMessage == null || commitMessage.trim().isEmpty();
        boolean skippedByCommitMessage = !commitMessageEmpty && commitMessage.contains(SKIP_RELEASE_KEYWORD);
        LOG.lifecycle("  Commit message to inspect for keyword '{}': {}",
                SKIP_RELEASE_KEYWORD,
                commitMessageEmpty? "<unknown commit message>" : "\n" + commitMessage);

        boolean releasableBranch = branch != null && branch.matches(releasableBranchRegex);
        LOG.lifecycle("  Current branch '{}' matches '{}': {}", branch, releasableBranchRegex, releasableBranch);

        boolean publicationsChanged = publicationsChanged();

        boolean releaseNotNeeded = !publicationsChanged || skipEnvVariable || skippedByCommitMessage || pullRequest || !releasableBranch;

        String message = "  Release is needed: " + !releaseNotNeeded +
                "\n    - skip by env variable: " + skipEnvVariable +
                "\n    - skip by commit message: " + skippedByCommitMessage +
                "\n    - is pull request build:  " + pullRequest +
                "\n    - is releasable branch:  " + releasableBranch +
                "\n    - publications changed since previous release:  " + publicationsChanged;

        if (releaseNotNeeded && explosive) {
            throw new GradleException(message);
        } else {
            LOG.lifecycle(message);
        }

        return !releaseNotNeeded;
    }

    private static boolean containsDifferences(File comparisonResult) {
        return comparisonResult.isFile() && comparisonResult.length() > 0;
    }

    @ExposedForTesting
    boolean publicationsChanged() {
        if (comparisonResults.isEmpty()) {
            return false;
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

    @ExposedForTesting
    void setEnvVariables(EnvVariables envVariables){
        this.envVariables = envVariables;
    }

    public void addComparisonResult(File comparisonResult) {
        comparisonResults.add(comparisonResult);
    }
}

package org.mockito.release.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.internal.comparison.PublicationsComparatorTask;

import java.util.LinkedList;
import java.util.List;

/**
 * Decides if the release is needed.
 * The release is <strong>not needed</strong> when any of below is true:
 *  - the env variable 'SKIP_RELEASE' is present
 *  - the commit message, loaded from 'TRAVIS_COMMIT_MESSAGE' env variable contains '[ci skip-release]' keyword
 *  - the env variable 'TRAVIS_PULL_REQUEST' is not empty, not an empty String and and not 'false'
 *  - the branch ({@link #getBranch()} does not match release-eligibility regex ({@link #getReleasableBranchRegex()}.
 *
 *  TODO update the javadoc
 */
public class ReleaseNeededTask extends DefaultTask {

    private final static Logger LOG = Logging.getLogger(ReleaseNeededTask.class);

    private final static String SKIP_RELEASE_ENV = "SKIP_RELEASE";
    private final static String SKIP_RELEASE_KEYWORD = "[ci skip-release]";

    private String branch;
    private String releasableBranchRegex;
    private final List<PublicationsComparatorTask> publicationsComparators = new LinkedList<PublicationsComparatorTask>();
    private String commitMessage;
    private boolean pullRequest;
    private boolean explosive;
    private boolean releaseNotNeeded;

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

    @TaskAction public void releaseNeeded() {
        boolean skipEnvVariable = System.getenv(SKIP_RELEASE_ENV) != null;
        LOG.lifecycle("  Environment variable {} present: {}", SKIP_RELEASE_ENV, skipEnvVariable);

        boolean commitMessageEmpty = commitMessage == null || commitMessage.trim().isEmpty();
        boolean skippedByCommitMessage = !commitMessageEmpty && commitMessage.contains(SKIP_RELEASE_KEYWORD);
        LOG.lifecycle("  Commit message to inspect for keyword '{}': {}",
                SKIP_RELEASE_KEYWORD,
                commitMessageEmpty? "<unknown commit message>" : "\n" + commitMessage);

        boolean releasableBranch = branch != null && branch.matches(releasableBranchRegex);
        LOG.lifecycle("  Current branch '{}' matches '{}': {}", branch, releasableBranchRegex, releasableBranch);

        boolean allPublicationsEqual = areAllPublicationsEqual();

        // add unit tests for release not needed
        releaseNotNeeded = allPublicationsEqual || skipEnvVariable || skippedByCommitMessage || pullRequest || !releasableBranch;

        String message = "  Release is needed: " + !releaseNotNeeded +
                "\n    - skip by env variable: " + skipEnvVariable +
                "\n    - skip by commit message: " + skippedByCommitMessage +
                "\n    - is pull request build:  " + pullRequest +
                "\n    - is releasable branch:  " + releasableBranch +
                "\n    - anything changed in publications since the previous release:  " + !allPublicationsEqual;

        if (releaseNotNeeded && explosive) {
            throw new GradleException(message);
        } else {
            LOG.lifecycle(message);
        }
    }

    public void addPublicationsComparator(PublicationsComparatorTask task) {
        this.dependsOn(task);
        publicationsComparators.add(task);
    }

    boolean areAllPublicationsEqual(){
        boolean allEqual = true;

        for(PublicationsComparatorTask comparator : publicationsComparators){
            allEqual &= comparator.isPublicationsEqual();
        }

        return allEqual;
    }

    boolean isReleaseNotNeeded(){
        return releaseNotNeeded;
    }
}

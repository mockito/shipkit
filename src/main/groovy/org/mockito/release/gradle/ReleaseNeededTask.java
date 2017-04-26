package org.mockito.release.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;

/**
 * Decides if the release is needed.
 * The release is <strong>not needed</strong> when any of below is true:
 *  - the env variable 'SKIP_RELEASE' is present
 *  - the commit message, loaded from 'TRAVIS_COMMIT_MESSAGE' env variable contains '[ci skip-release]' keyword
 *  - the env variable 'TRAVIS_PULL_REQUEST' is not empty, not an empty String and and not 'false'
 *  - the branch ({@link #getBranch()} does not match release-eligibility regex ({@link #getReleasableBranchRegex()}.
 */
public class ReleaseNeededTask extends DefaultTask {

    private final static Logger LOG = Logging.getLogger(ReleaseNeededTask.class);

    //TODO we should consider exposing the configuration below in the ReleaseConfiguration
    //For example, 'releasing.git.commit.message', 'releasing.git.commit.skipReleaseKeyword', 'releasing.skip', 'releasing.git.pullRequest'
    //Goal: have all configuration in the extension object, have all env variable handling there for centralized documentation

    private final static String SKIP_RELEASE_ENV = "SKIP_RELEASE";
    private final static String COMMIT_MESSAGE_ENV = "TRAVIS_COMMIT_MESSAGE";
    private final static String PULL_REQUEST_ENV = "TRAVIS_PULL_REQUEST";
    private final static String SKIP_RELEASE_KEYWORD = "[ci skip-release]";

    private String branch;
    private String releasableBranchRegex;
    private boolean allPublicationsEqual;

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

    public boolean isAllPublicationsEqual() {
        return allPublicationsEqual;
    }

    public void setAllPublicationsEqual(boolean allPublicationsEqual) {
        this.allPublicationsEqual = allPublicationsEqual;
    }

    /**
     * See {@link #getReleasableBranchRegex()}
     */
    public void setReleasableBranchRegex(String releasableBranchRegex) {
        this.releasableBranchRegex = releasableBranchRegex;
    }

    @TaskAction public void releaseNeeded() {
        boolean skipEnvVariable = System.getenv(SKIP_RELEASE_ENV) != null;
        String commitMessage = System.getenv(COMMIT_MESSAGE_ENV);
        boolean skippedByCommitMessage = commitMessage != null && commitMessage.contains(SKIP_RELEASE_KEYWORD);

        //returns true only if pull request env variable points to PR number
        String pr = System.getenv(PULL_REQUEST_ENV);
        boolean pullRequest = pr != null && !pr.trim().isEmpty() && !pr.equals("false");

        boolean releasableBranch = branch != null && branch.matches(releasableBranchRegex);

        boolean notNeeded = allPublicationsEqual || skipEnvVariable || skippedByCommitMessage || pullRequest || !releasableBranch;

        //TODO add more color to the message
        //add env variable names, what is the current branch, what is the regexp, etc.
        //This way it is easier to understand how stuff works by reading the log
        String message = "  Release is needed: " + !notNeeded +
                "\n    - skip by env variable: " + skipEnvVariable +
                "\n    - skip by commit message: " + skippedByCommitMessage +
                "\n    - is pull request build:  " + pullRequest +
                "\n    - is releasable branch:  " + releasableBranch +
                "\n    - anything changed in publications since the last release:  " + allPublicationsEqual;

        if (notNeeded) {
            throw new GradleException(message);
        } else {
            LOG.lifecycle(message);
        }
    }
}

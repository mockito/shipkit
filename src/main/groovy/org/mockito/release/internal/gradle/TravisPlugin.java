package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.gradle.util.TaskMaker;

/**
 * Configures the release automation to be used with Travis CI.
 * Preconfigures "releasing.build.*" settings based on Travis env variables.
 * <p>
 * Adds tasks:
 *
 * <ul>
 *     <li>'travisReleasePrepare' - Prepares the working copy for releasing using Travis CI</li>
 * </ul>
 */
public class TravisPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(GitPlugin.class);
        ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        String buildNo = System.getenv("TRAVIS_BUILD_NUMBER");
        if (buildNo != null) {
            conf.getGit().setCommitMessagePostfix("by Travis CI build " + buildNo + " [ci skip]");
        } else {
            conf.getGit().setCommitMessagePostfix("[ci skip]");
        }

        String pr = System.getenv("TRAVIS_PULL_REQUEST");
        boolean isPullRequest = pr != null && !pr.trim().isEmpty() && !pr.equals("false");

        conf.getBuild().setCommitMessage(System.getenv("TRAVIS_COMMIT_MESSAGE"));
        conf.getBuild().setBranch(System.getenv("TRAVIS_BRANCH"));
        //TODO until we implement logic that gets the current branch we require to set TRAVIS_BRANCH even for local testing
        //This is very annoying.
        //We should create a utilty class/method (GitUtil) that identifies the branch by forking off git process.
        //This needs to be synchronized and needs to happen only once.
        //The utility method should use the build.branch setting but if it is not provided, get it from git

        conf.getBuild().setPullRequest(isPullRequest);

        TaskMaker.task(project, "travisReleasePrepare", new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Prepares the working copy for releasing using Travis CI");
                t.dependsOn(GitPlugin.UNSHALLOW_TASK, GitPlugin.CHECKOUT_BRANCH_TASK, GitPlugin.SET_USER_TASK, GitPlugin.SET_EMAIL_TASK);
            }
        });
    }
}

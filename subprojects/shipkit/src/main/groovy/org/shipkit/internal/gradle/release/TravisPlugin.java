package org.shipkit.internal.gradle.release;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.git.GitCommitTask;
import org.shipkit.gradle.git.IdentifyGitBranchTask;
import org.shipkit.gradle.release.ReleaseNeededTask;
import org.shipkit.internal.gradle.configuration.BasicValidator;
import org.shipkit.internal.gradle.configuration.LazyConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitBranchPlugin;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.git.GitSetupPlugin;
import org.shipkit.internal.gradle.git.tasks.GitCheckOutTask;
import org.shipkit.internal.gradle.util.StringUtil;

import static java.lang.System.getenv;
import static org.shipkit.internal.gradle.util.TravisCommitMessageUtils.decorateCommitMessagePostfix;

/**
 * Configures the release automation to be used with Travis CI.
 * Intended for root project.
 * <p>
 * Applies:
 * <ul>
 *     <li>{@link CiReleasePlugin}</li>
 * </ul>
 * Adds behavior:
 * <ul>
 * <li>Configures {@link GitBranchPlugin}/{@link IdentifyGitBranchTask}
 *      so that the branch information is taken from 'TRAVIS_BRANCH' env variable.</li>
 * <li>Configures {@link GitSetupPlugin}/{@link GitCheckOutTask}
 *      so that it checks out the branch specified in env variable.</li>
 * <li>Configures {@link GitSetupPlugin}/{@link org.shipkit.gradle.git.GitCommitTask}
 *      so that it enrich commit postfix with url to Travis build</li>
 * <li>Configures {@link ReleaseNeededPlugin}/{@link ReleaseNeededTask}
 *      so that it uses information from 'TRAVIS_PULL_REQUEST' and 'TRAVIS_COMMIT_MESSAGE' env variables.</li>
 * </ul>
 */
public class TravisPlugin implements Plugin<Project> {

    private final static Logger LOG = Logging.getLogger(TravisPlugin.class);

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(CiReleasePlugin.class);
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        final String branch = System.getenv("TRAVIS_BRANCH");
        LOG.info("Branch from 'TRAVIS_BRANCH' env variable: {}", branch);

        //configure branch based on Travis' env variable
        IdentifyGitBranchTask identifyBranch = (IdentifyGitBranchTask) project.getTasks().getByName(GitBranchPlugin.IDENTIFY_GIT_BRANCH);
        if (!StringUtil.isEmpty(branch)) {
            identifyBranch.setBranch(branch);
        }

        //set the branch to be checked out on ci build
        final GitCheckOutTask checkout = (GitCheckOutTask) project.getTasks().getByName(GitSetupPlugin.CHECKOUT_TASK);
        checkout.setRev(branch);
        LazyConfiguration.lazyConfiguration(checkout, () -> BasicValidator.notNull(checkout.getRev(),
                "Task " + checkout.getPath() + " does not know the target revision to check out.\n" +
                "In Travis CI builds, it is automatically configured from 'TRAVIS_BRANCH' environment variable.\n" +
                "If you are trying to run this task outside Travis, you can export the environment variable.\n" +
                "Alternatively, you can set the task's 'rev' property explicitly."));

        //update release needed task based on Travis' env variables
        String pr = System.getenv("TRAVIS_PULL_REQUEST");
        String buildNumber = getenv("TRAVIS_BUILD_NUMBER");
        LOG.info("Pull request from 'TRAVIS_PULL_REQUEST' env variable: {}", pr);
        final boolean isPullRequest = pr != null && !pr.trim().isEmpty() && !pr.equals("false");
        LOG.info("Pull request build: {}", isPullRequest);
        String commitMessage = decorateCommitMessagePostfix(conf, buildNumber);

        GitCommitTask gitCommitTask = (GitCommitTask) project.getTasks().getByName(GitPlugin.GIT_COMMIT_TASK);
        if (!StringUtil.isEmpty(commitMessage)) {
            gitCommitTask.setCommitMessagePostfix(commitMessage);
        }

        project.getTasks().withType(ReleaseNeededTask.class, t -> {
            t.setCommitMessage(System.getenv("TRAVIS_COMMIT_MESSAGE"));
            t.setPullRequest(isPullRequest);
        });
    }
}

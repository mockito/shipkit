package org.shipkit.internal.gradle.release;

import org.apache.commons.lang.StringUtils;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.git.IdentifyGitBranchTask;
import org.shipkit.gradle.release.ReleaseNeededTask;
import org.shipkit.internal.gradle.configuration.BasicValidator;
import org.shipkit.internal.gradle.configuration.LazyConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitBranchPlugin;
import org.shipkit.internal.gradle.git.GitSetupPlugin;
import org.shipkit.internal.gradle.git.tasks.GitCheckOutTask;
import org.shipkit.internal.gradle.release.ci.GenericProvider;
import org.shipkit.internal.gradle.release.ci.GitHubActionsProvider;
import org.shipkit.internal.gradle.release.ci.TravisProvider;
import org.shipkit.internal.gradle.util.StringUtil;

/**
 * Configures the release automation to be used with GitHub Actions. Intended for root project.
 * <p>
 * Applies:
 * <ul>
 *     <li>{@link CiReleasePlugin}</li>
 * </ul>
 * Adds behavior:
 * <ul>
 * <li>Configures {@link GitBranchPlugin}/{@link IdentifyGitBranchTask}
 *      so that the branch information is taken from 'GITHUB_REF' env variable.</li>
 * <li>Configures {@link GitSetupPlugin}/{@link GitCheckOutTask}
 *      so that it checks out the branch specified in env variable.</li>
 * <li>Configures {@link ReleaseNeededPlugin}/{@link ReleaseNeededTask}
 *      so that it uses information from 'GITHUB_EVENT' and 'GITHUB_EVENT_PATH' env variables.</li>
 * </ul>
 */
public class CiPlugin implements Plugin<Project> {

    private final static Logger LOG = Logging.getLogger(CiPlugin.class);

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(CiReleasePlugin.class);
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        final CiProvider ciProvider;
        switch (conf.getCiManagement().getSystem()) {
            case GitHubActionsProvider.NAME:
                ciProvider = new GitHubActionsProvider(conf);
                break;
            case TravisProvider.NAME:
                ciProvider = new TravisProvider(conf);
                break;
            default:
                ciProvider = new GenericProvider(conf);
        }

        final String branch = ciProvider.getBranch();
        LOG.info("Branch from '{}': {}", ciProvider.getName(), branch);

        //configure branch based on GitHubAction' env variable
        IdentifyGitBranchTask identifyBranch = (IdentifyGitBranchTask) project.getTasks().getByName(GitBranchPlugin.IDENTIFY_GIT_BRANCH);
        if (!StringUtil.isEmpty(branch)) {
            identifyBranch.setBranch(branch);
        }

        //set the branch to be checked out on ci build
        final GitCheckOutTask checkout = (GitCheckOutTask) project.getTasks().getByName(GitSetupPlugin.CHECKOUT_TASK);
        checkout.setRev(branch);
        LazyConfiguration.lazyConfiguration(checkout, new Runnable() {
            public void run() {
                BasicValidator.notNull(checkout.getRev(),
                                       "Task " + checkout.getPath() + " does not know the target revision to check out.\n" +
                                           "In '" + ciProvider.getName() + "' builds, it is automatically configured from " + ciProvider.getBranchDescription() + "\n" +
                                           "Alternatively, you can set the task's 'rev' property explicitly.");
            }
        });

        final boolean isPullRequest = ciProvider.isPullRequest();
        LOG.info("Pull request from '{}': {}", ciProvider.getName(), isPullRequest);

        final String commitMessage = ciProvider.getCommitMessage();

        project.getTasks().withType(ReleaseNeededTask.class, new Action<ReleaseNeededTask>() {
            public void execute(ReleaseNeededTask t) {
                t.setCommitMessage(commitMessage);
                t.setPullRequest(isPullRequest);
            }
        });
    }
}

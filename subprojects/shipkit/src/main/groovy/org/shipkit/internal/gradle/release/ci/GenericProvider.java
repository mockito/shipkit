package org.shipkit.internal.gradle.release.ci;

import org.apache.commons.lang.StringUtils;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.git.IdentifyGitBranchTask;
import org.shipkit.gradle.release.ReleaseNeededTask;
import org.shipkit.internal.gradle.configuration.BasicValidator;
import org.shipkit.internal.gradle.git.GitBranchPlugin;
import org.shipkit.internal.gradle.git.GitSetupPlugin;
import org.shipkit.internal.gradle.git.tasks.GitCheckOutTask;
import org.shipkit.internal.gradle.release.CiProvider;
import org.shipkit.internal.gradle.release.CiReleasePlugin;
import org.shipkit.internal.gradle.release.ReleaseNeededPlugin;

/**
 * Configures the release automation to be used with any CI. Intended for root project.
 * <p>
 * Applies:
 * <ul>
 *     <li>{@link CiReleasePlugin}</li>
 * </ul>
 * Adds behavior:
 * <ul>
 * <li>Configures {@link GitBranchPlugin}/{@link IdentifyGitBranchTask}
 *      so that the branch information is taken from 'CI_BRANCH' env variable.</li>
 * <li>Configures {@link GitSetupPlugin}/{@link GitCheckOutTask}
 *      so that it checks out the branch specified in env variable.</li>
 * <li>Configures {@link ReleaseNeededPlugin}/{@link ReleaseNeededTask}
 *      so that it uses information from 'CI_PULL_REQUEST' and 'CI_COMMIT_MESSAGE' env variables.</li>
 * </ul>
 */
public class GenericProvider implements CiProvider {

    public static final String NAME = "Generic CI";
    private static final Logger LOG = Logging.getLogger(GenericProvider.class);

    private final String name;

    public GenericProvider(ShipkitConfiguration conf) {
        BasicValidator.notNull(conf.getCiManagement().getUrl(),
                               "You are using the Generic Ci Provider and did not define a CI url!\n"
                                   + "Please set the 'ciManagement.url' in your 'shipkit.gradle'");
        name = StringUtils.defaultIfEmpty(conf.getCiManagement().getSystem(), NAME);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCommitMessage() {
        return System.getenv("CI_COMMIT_MESSAGE");
    }

    @Override
    public boolean isPullRequest() {
        String pr = System.getenv("CI_PULL_REQUEST");
        LOG.info("Pull request from 'CI_PULL_REQUEST' env variable: {}", pr);
        return pr != null && !pr.trim().isEmpty() && !pr.equals("false");
    }

    @Override
    public String getBranch() {
        final String branch = System.getenv("CI_BRANCH");
        LOG.info("Branch from 'CI_BRANCH' env variable: {}", branch);
        return branch;
    }

    @Override
    public String getBranchDescription() {
        return "'CI_BRANCH' environment variable.\n" +
            "If you are trying to run this task outside a CI, you can export the environment variable.\n";
    }
}

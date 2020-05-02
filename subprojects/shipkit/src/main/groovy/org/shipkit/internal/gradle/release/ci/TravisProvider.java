package org.shipkit.internal.gradle.release.ci;

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
import org.shipkit.internal.gradle.git.GitBranchPlugin;
import org.shipkit.internal.gradle.git.GitSetupPlugin;
import org.shipkit.internal.gradle.git.tasks.GitCheckOutTask;
import org.shipkit.internal.gradle.release.CiProvider;
import org.shipkit.internal.gradle.release.CiReleasePlugin;
import org.shipkit.internal.gradle.release.ReleaseNeededPlugin;
import org.shipkit.internal.gradle.util.StringUtil;

/**
 * Configures the release automation to be used with Travis CI. Intended for root project.
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
 * <li>Configures {@link ReleaseNeededPlugin}/{@link ReleaseNeededTask}
 *      so that it uses information from 'TRAVIS_PULL_REQUEST' and 'TRAVIS_COMMIT_MESSAGE' env variables.</li>
 * </ul>
 */
public class TravisProvider implements CiProvider {

    public static final String NAME = "TravisCI";
    private static final Logger LOG = Logging.getLogger(TravisProvider.class);

    public TravisProvider(ShipkitConfiguration conf) {
        conf.getCiManagement().setSystem(NAME);
        conf.getCiManagement().setUrl("https://travis-ci.org/" + conf.getGitHub().getRepository());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCommitMessage() {
        return System.getenv("TRAVIS_COMMIT_MESSAGE");
    }

    @Override
    public boolean isPullRequest() {
        String pr = System.getenv("TRAVIS_PULL_REQUEST");
        LOG.info("Pull request from 'TRAVIS_PULL_REQUEST' env variable: {}", pr);
        return pr != null && !pr.trim().isEmpty() && !pr.equals("false");
    }

    @Override
    public String getBranch() {
        final String branch = System.getenv("TRAVIS_BRANCH");
        LOG.info("Branch from 'TRAVIS_BRANCH' env variable: {}", branch);
        return branch;
    }

    @Override
    public String getBranchDescription() {
        return "'TRAVIS_BRANCH' environment variable.\n" +
            "If you are trying to run this task outside Travis, you can export the environment variable.\n";
    }
}

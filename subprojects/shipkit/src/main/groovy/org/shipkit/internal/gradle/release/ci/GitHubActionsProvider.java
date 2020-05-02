package org.shipkit.internal.gradle.release.ci;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.DeserializationException;
import org.json.simple.Jsoner;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.git.IdentifyGitBranchTask;
import org.shipkit.gradle.release.ReleaseNeededTask;
import org.shipkit.internal.gradle.git.GitBranchPlugin;
import org.shipkit.internal.gradle.git.GitSetupPlugin;
import org.shipkit.internal.gradle.git.tasks.GitCheckOutTask;
import org.shipkit.internal.gradle.release.CiProvider;
import org.shipkit.internal.gradle.release.CiReleasePlugin;
import org.shipkit.internal.gradle.release.ReleaseNeededPlugin;

import java.io.FileReader;
import java.io.IOException;

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
 *      so that it uses information from 'GITHUB_EVENT_NAME' env variable
 *      and parses the event json for the commit message.</li>
 * </ul>
 */
public class GitHubActionsProvider implements CiProvider {

    public static final String NAME = "GitHub Actions";
    private static final Logger LOG = Logging.getLogger(GitHubActionsProvider.class);

    private final String commitMessage;

    public GitHubActionsProvider(ShipkitConfiguration conf) {
        conf.getCiManagement().setSystem(NAME);
        conf.getCiManagement().setUrl("https://github.com/" + conf.getGitHub().getRepository()+"/actions");
        String commitMessage = null;
        try (FileReader reader = new FileReader(System.getenv("GITHUB_EVENT"))){
            final Object deserialize =  Jsoner.deserialize(reader);
            final JSONObject githubEventData = JSONObject.fromObject(deserialize);

            final JSONObject headCommit = githubEventData.getJSONObject("head_commit");
            if (headCommit != null) {
                commitMessage = headCommit.getString("message");
            } else {
                LOG.warn("Could not determine commit message!");
            }
        } catch (IOException | DeserializationException e) {
            LOG.warn("Could not determine commit message!", e);
        }
        this.commitMessage = commitMessage;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getCommitMessage() {
        return commitMessage;
    }

    @Override
    public boolean isPullRequest() {
        String pr = System.getenv("GITHUB_EVENT_NAME");
        LOG.info("Pull request from 'GITHUB_EVENT_NAME' env variable: {}", pr);
        return StringUtils.equals("pull_request", pr);
    }

    @Override
    public String getBranch() {
        final String githubRef = System.getenv("GITHUB_REF");
        final String branch = StringUtils.remove(githubRef, "refs/heads/");
        LOG.info("Branch from 'GITHUB_REF' env variable: {} sanitized to {}", githubRef, branch);
        return branch;
    }

    @Override
    public String getBranchDescription() {
        return "'GITHUB_REF' environment variable.\n" +
            "If you are trying to run this task outside "+NAME+", you can export the environment variable.\n";
    }
}

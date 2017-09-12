package org.shipkit.internal.gradle.git.tasks;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.exec.DefaultProcessRunner;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.util.ExposedForTesting;

import javax.inject.Inject;

/**
 * Task that computes git origin repository.
 * Shouldn't be used directly, but through {@link org.shipkit.internal.gradle.git.GitAuthPlugin#provideAuthTo(Task, Action)}
 */
public class IdentifyGitOriginRepoTask extends DefaultTask {

    private final static Logger LOG = Logging.getLogger(IdentifyGitOriginRepoTask.class);
    final static String FALLBACK_GITHUB_REPO = "mockito/shipkit-example";

    private String originRepo;

    private GitOriginRepoProvider originRepoProvider;

    @Inject
    public IdentifyGitOriginRepoTask() {
        originRepoProvider = new GitOriginRepoProvider(new DefaultProcessRunner(getProject().getProjectDir()));
    }

    @TaskAction
    public void identifyGitOriginRepo() {
        if (originRepo != null) {
            LOG.lifecycle("  Using Git origin repository configured on the task: {}", originRepo);
            return;
        }

        ShipkitConfigurationPlugin plugin = getProject().getPlugins().findPlugin(ShipkitConfigurationPlugin.class);
        if (plugin != null) {
            originRepo = plugin.getConfiguration().getLenient().getGitHub().getRepository();
            if (originRepo != null) {
                LOG.lifecycle("  Using Git origin repository from Shipkit configuration: {}", originRepo);
                return;
            }
        }

        try {
            originRepo = originRepoProvider.getOriginGitRepo();
            LOG.lifecycle("  Identified Git origin repository: " + originRepo);
        } catch (Exception e) {
            LOG.lifecycle("  Problems getting url of git remote origin (run with --debug to see stack trace).\n" +
                "  Using fallback '" + FALLBACK_GITHUB_REPO + "' instead.\n" +
                "  GitHub repository can be configured in in shipkit file.\n");
            LOG.debug("  Problems getting url of git remote origin", e);
            originRepo = FALLBACK_GITHUB_REPO;
        }
    }

    /**
     * Git remote origin repo in a format "user/repo", eg. "mockito/shipkit".
     * Instead of setting this repository on a task, configure it in the shipkit file,
     * using {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#setRepository(String)}
     * Internal classes and other tasks should get the repo via {@link org.shipkit.internal.gradle.git.GitAuthPlugin#provideAuthTo(Task, Action)}
     */
    public String getOriginRepo() {
        return originRepo;
    }

    /**
     * See {@link #getOriginRepo()}
     */
    public void setOriginRepo(String originRepo) {
        this.originRepo = originRepo;
    }

    @ExposedForTesting
    void setOriginRepoProvider(GitOriginRepoProvider originRepoProvider) {
        this.originRepoProvider = originRepoProvider;
    }
}

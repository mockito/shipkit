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

    String repository;

    private GitOriginRepoProvider originRepoProvider;

    @Inject
    public IdentifyGitOriginRepoTask() {
        originRepoProvider = new GitOriginRepoProvider(new DefaultProcessRunner(getProject().getProjectDir()));
    }

    @TaskAction
    public void identifyGitOriginRepo() {
        ShipkitConfigurationPlugin plugin = getProject().getPlugins().findPlugin(ShipkitConfigurationPlugin.class);
        if (plugin != null) {
            repository = plugin.getConfiguration().getLenient().getGitHub().getRepository();
            if (repository != null) {
                LOG.info("  Using Git origin repository from Shipkit configuration: {}", repository);
                return;
            }
        }

        try {
            repository = originRepoProvider.getOriginGitRepo();
            if (plugin != null) {
                plugin.getConfiguration().getGitHub().setRepository(getRepository());
            }
            LOG.lifecycle("  Identified Git origin repository: " + repository);
        } catch (Exception e) {
            LOG.lifecycle("  Problems getting url of git remote origin (run with --debug to see stack trace).\n" +
                "  Using fallback '" + FALLBACK_GITHUB_REPO + "' instead.\n" +
                "  GitHub repository can be configured in in shipkit file.\n");
            LOG.debug("  Problems getting url of git remote origin", e);
            repository = FALLBACK_GITHUB_REPO;
        }
    }

    /**
     * Git remote origin repo in a format "user/repo", eg. "mockito/shipkit".
     * Instead of setting this repository on a task, configure it in the shipkit file,
     * using {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#setRepository(String)}
     * Internal classes and other tasks should get the repo via {@link org.shipkit.internal.gradle.git.GitAuthPlugin#provideAuthTo(Task, Action)}
     */
    public String getRepository() {
        return repository;
    }

    @ExposedForTesting
    void setOriginRepoProvider(GitOriginRepoProvider originRepoProvider) {
        this.originRepoProvider = originRepoProvider;
    }
}

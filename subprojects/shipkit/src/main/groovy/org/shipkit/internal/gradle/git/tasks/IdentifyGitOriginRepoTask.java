package org.shipkit.internal.gradle.git.tasks;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.exec.DefaultProcessRunner;
import org.shipkit.internal.gradle.git.GitOriginPlugin;
import org.shipkit.internal.util.ExposedForTesting;

import javax.inject.Inject;

/**
 * Task that identifies git origin repository.
 * See {@link #getRepository()}
 */
public class IdentifyGitOriginRepoTask extends DefaultTask {

    private final static Logger LOG = Logging.getLogger(IdentifyGitOriginRepoTask.class);
    final static String FALLBACK_GITHUB_REPO = "mockito/shipkit-example";

    private String repository;

    private GitOriginRepoProvider originRepoProvider;

    @Inject
    public IdentifyGitOriginRepoTask() {
        originRepoProvider = new GitOriginRepoProvider(new DefaultProcessRunner(getProject().getProjectDir()));
    }

    @TaskAction
    public void identifyGitOriginRepo() {
        if (repository != null) {
            //Useful to override the default behavior or supply origin repository using some custom logic.
            //See javadoc for "setRepository(String repository)" method.
            LOG.lifecycle("  No need to identify Git origin repository because it was set directly on the task. Repository: {}", repository);
            return;
        }

        try {
            repository = originRepoProvider.getOriginGitRepo();
            LOG.lifecycle("  Identified Git origin repository: " + repository);
        } catch (Exception e) {
            //TODO SF instead of fallback, let's fail and suggest the user what to do.
            LOG.lifecycle("  Problems getting url of git remote origin (run with --debug to see stack trace).\n" +
                "  Using fallback '" + FALLBACK_GITHUB_REPO + "' instead.\n" +
                "  GitHub repository can be configured in in shipkit file.\n");
            LOG.debug("  Problems getting url of git remote origin", e);
            repository = FALLBACK_GITHUB_REPO;
        }
    }

    /**
     * Git remote origin repo in a format "user/repo", eg. "mockito/shipkit".
     * If you call this getter, ensure that the task has executed first.
     * Internal classes and other tasks should get the repo via {@link GitOriginPlugin#provideOriginRepo(Task, Action)}
     * See also {@link #setRepository(String)}
     */
    public String getRepository() {
        //TODO SF graceful failure explaining that the task did not run yet
        return repository;
    }

    /**
     * Git remote origin repo in a format "user/repo", eg. "mockito/shipkit".
     * You set the value to override the default behavior of the task and avoid forking off 'git' process to identify the repository.
     * Might be useful for edge cases.
     * For example, if the user has custom logic to identify repository.
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }

    @ExposedForTesting
    void setOriginRepoProvider(GitOriginRepoProvider originRepoProvider) {
        this.originRepoProvider = originRepoProvider;
    }
}

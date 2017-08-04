package org.shipkit.internal.gradle.git.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.exec.DefaultProcessRunner;
import org.shipkit.internal.util.ExposedForTesting;
import org.shipkit.internal.util.ResultHandler;
import org.shipkit.internal.gradle.git.GitRemoteOriginPlugin;

import javax.inject.Inject;

/**
 * Task that computes git origin repository.
 * Shouldn't be used directly, but through {@link GitRemoteOriginPlugin#chooseHandlerForOriginResult(IdentifyGitOriginRepoTask, ResultHandler)}
 */
public class IdentifyGitOriginRepoTask extends DefaultTask{

    private String originRepo;
    private RuntimeException executionException;

    private GitOriginRepoProvider originRepoProvider;

    @Inject
    public IdentifyGitOriginRepoTask(){
        originRepoProvider = new GitOriginRepoProvider(new DefaultProcessRunner(getProject().getProjectDir()));
    }

    @TaskAction
    public void identifyGitOriginRepo(){
        if(originRepo == null){
            try {
                originRepo = originRepoProvider.getOriginGitRepo();
            } catch(RuntimeException e){
                executionException = e;
            }
        }
    }

    /**
     * Git remote origin repo in a format "user/repo", eg. "mockito/shipkit".
     * Shouldn't be used directly, see {@link GitRemoteOriginPlugin#chooseHandlerForOriginResult(IdentifyGitOriginRepoTask, ResultHandler)}
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

    /**
     * Exception that was thrown (if any) during computation of git origin repo.
     */
    public RuntimeException getExecutionException() {
        return executionException;
    }

    /**
     * See {@link #getExecutionException()}
     */
    public void setExecutionException(RuntimeException executionException) {
        this.executionException = executionException;
    }

    @ExposedForTesting
    void setOriginRepoProvider(GitOriginRepoProvider originRepoProvider){
        this.originRepoProvider = originRepoProvider;
    }
}

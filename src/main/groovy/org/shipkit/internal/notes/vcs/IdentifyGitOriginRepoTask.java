package org.shipkit.internal.notes.vcs;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.exec.DefaultProcessRunner;

import javax.inject.Inject;

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

    public String getOriginRepo() {
        return originRepo;
    }

    public void setOriginRepo(String originRepo) {
        this.originRepo = originRepo;
    }

    public RuntimeException getExecutionException() {
        return executionException;
    }

    public void setExecutionException(RuntimeException executionException) {
        this.executionException = executionException;
    }

    void setOriginRepoProvider(GitOriginRepoProvider originRepoProvider){
        this.originRepoProvider = originRepoProvider;
    }
}

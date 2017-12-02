package org.shipkit.internal.gradle.util.handler;

import org.gradle.api.Action;
import org.shipkit.gradle.git.GitPushTask;
import org.shipkit.internal.gradle.util.handler.exceptions.CannotPushToGithubException;

public class GitPushTaskExceptionHandler implements Action<RuntimeException> {

    public static final String GH_WRITE_TOKEN_NOT_SET_MSG = "Cannot push to remote repository. GH_WRITE_TOKEN env variable not set or you don't have write access to remote. Please recheck your configuration.";
    public static final String GH_WRITE_TOKEN_INVALID_MSG = "Cannot push to remote repository. GH_WRITE_TOKEN env variable is set but possibly invalid. Please recheck your configuration.";

    private GitPushTask gitPushTask;

    public GitPushTaskExceptionHandler(GitPushTask gitPushTask) {
        this.gitPushTask = gitPushTask;
    }

    private RuntimeException create(Exception e) {
        String message;
        if (gitPushTask.getSecretValue() == null) {
            message = GH_WRITE_TOKEN_NOT_SET_MSG;
        } else {
            message = GH_WRITE_TOKEN_INVALID_MSG;
        }
        return new CannotPushToGithubException(message, e);
    }

    protected boolean matchException(Exception e) {
        return e.getMessage().contains("Authentication failed") || e.getMessage().contains("unable to access");
    }

    @Override
    public void execute(RuntimeException ex) {
        if (matchException(ex)) {
            throw create(ex);
        } else {
            throw ex;
        }
    }
}

package org.shipkit.internal.gradle.git.tasks;

import java.util.LinkedList;
import java.util.List;

import org.shipkit.gradle.git.GitPushTask;
import org.shipkit.internal.exec.DefaultProcessRunner;
import org.shipkit.internal.gradle.util.handler.GitPushTaskExceptionHandler;
import org.shipkit.internal.gradle.util.handler.ProcessExceptionHandler;

/**
 * Utility class for configuring git push task with the correct git push arguments.
 */
public class GitPush {
    /**
     * Constructs git push arguments based of the url, targets and dry run
     */
    static List<String> gitPushArgs(String url, List<String> targets, boolean dryRun) {
        List<String> args = new LinkedList<String>();
        args.add("git");
        args.add("push");
        args.add(url);
        args.addAll(targets);
        if (dryRun) {
            args.add("--dry-run");
        }
        return args;
    }

    public void gitPush(final GitPushTask task) {
        TokenAvailabilityMessage.logMessage("git push", task.getSecretValue());

        Runnable processRunner = new Runnable() {
            public void run() {
                new DefaultProcessRunner(task.getProject().getProjectDir())
                    .setSecretValue(task.getSecretValue())
                    .run(GitPush.gitPushArgs(task.getUrl(), task.getTargets(), task.isDryRun()));
            }
        };

        ProcessExceptionHandler exceptionHandler = new ProcessExceptionHandler();

        exceptionHandler.addHandler(new GitPushTaskExceptionHandler(task));
        exceptionHandler.runProcessExceptionally(processRunner);
    }

}

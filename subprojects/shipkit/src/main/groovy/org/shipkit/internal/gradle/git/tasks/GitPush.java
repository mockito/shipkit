package org.shipkit.internal.gradle.git.tasks;

import org.shipkit.gradle.git.GitPushTask;
import org.shipkit.internal.exec.DefaultProcessRunner;
import org.shipkit.internal.gradle.util.handler.GitPushExceptionHandler;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.shipkit.internal.gradle.util.handler.ExceptionHandling.withExceptionHandling;

/**
 * Utility class for configuring git push task with the correct git push arguments.
 */
public class GitPush {
    /**
     * Constructs git push arguments based of the url, targets and dry run
     */
    static List<String> gitPushArgs(String url, List<String> targets, boolean dryRun) {
        List<String> args = new LinkedList<>();
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

        Runnable gitPush = new Runnable() {
            public void run() {
                new DefaultProcessRunner(getWorkDir(task))
                    .setSecretValue(task.getSecretValue())
                    .run(GitPush.gitPushArgs(task.getUrl(), task.getTargets(), task.isDryRun()));
            }
        };

        withExceptionHandling(gitPush, new GitPushExceptionHandler(task.getSecretValue()));
    }

    private File getWorkDir(GitPushTask task) {
        if (task.getWorkingDir() != null) {
            return new File(task.getWorkingDir());
        }
        return task.getProject().getProjectDir();
    }
}

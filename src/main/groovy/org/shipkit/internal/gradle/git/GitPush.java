package org.shipkit.internal.gradle.git;

import org.shipkit.gradle.git.GitPushTask;
import org.shipkit.internal.exec.DefaultProcessRunner;
import org.shipkit.internal.gradle.git.tasks.TokenAvailabilityMessage;

import java.util.LinkedList;
import java.util.List;

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

    public void gitPush(GitPushTask task) {
        TokenAvailabilityMessage.logMessage("git push", task.getSecretValue());

        new DefaultProcessRunner(task.getProject().getProjectDir())
                .setSecretValue(task.getSecretValue())
                .run(GitPush.gitPushArgs(task.getUrl(), task.getTargets(), task.isDryRun()));
    }
}

package org.shipkit.internal.gradle.git.tasks;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.git.GitPushTask;
import org.shipkit.internal.exec.DefaultProcessRunner;
import org.shipkit.internal.gradle.util.CannotPushToGithubException;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for configuring git push task with the correct git push arguments.
 */
public class GitPush {
    private final static Logger LOG = Logging.getLogger(TokenAvailabilityMessage.class);

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
        try {
            new DefaultProcessRunner(task.getProject().getProjectDir())
                .setSecretValue(task.getSecretValue())
                .run(GitPush.gitPushArgs(task.getUrl(), task.getTargets(), task.isDryRun()));
        } catch (GradleException e) {
            if (CannotPushToGithubException.matchException(e)) {
                throw CannotPushToGithubException.create(e, task);
            } else {
                throw e;
            }
        }
    }

}

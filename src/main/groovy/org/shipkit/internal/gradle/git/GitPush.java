package org.shipkit.internal.gradle.git;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.git.GitPushTask;
import org.shipkit.internal.exec.DefaultProcessRunner;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for configuring git push task with the correct git push arguments.
 */
public class GitPush {

    private final static Logger LOG = Logging.getLogger(GitPush.class);

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

    /**
     * Configures url on the git push task, ensuring secrecy of the write token.
     * Write token is optional.
     */
    public static void setPushUrl(GitPushTask pushTask, ShipkitConfiguration conf) {
        String ghUser = conf.getGitHub().getWriteAuthUser();
        String ghRepo = conf.getGitHub().getRepository();
        String writeToken = conf.getLenient().getGitHub().getWriteAuthToken();
        setPushUrl(pushTask, writeToken, ghUser, ghRepo);
    }

    static void setPushUrl(GitPushTask pushTask, String writeToken, String ghUser, String ghRepo) {
        if (writeToken != null) {
            String url = MessageFormat.format("https://{0}:{1}@github.com/{2}.git", ghUser, writeToken, ghRepo);
            pushTask.setUrl(url);
            pushTask.setSecretValue(writeToken);
        } else {
            LOG.lifecycle("  'git push' does not use GitHub auth token because it was not specified.");
            String url = MessageFormat.format("https://github.com/{0}.git", ghRepo);
            pushTask.setUrl(url);
        }
    }

    public void gitPush(GitPushTask task) {
        new DefaultProcessRunner(task.getProject().getProjectDir())
                .setSecretValue(task.getSecretValue())
                .run(GitPush.gitPushArgs(task.getUrl(), task.getTargets(), task.isDryRun()));
    }
}

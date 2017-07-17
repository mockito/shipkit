package org.shipkit.internal.gradle.git;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.ReleaseConfiguration;
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
    public static List<String> gitPushArgs(String url, List<String> targets, boolean dryRun) {
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
    public static void setPushUrl(GitPushTask pushTask, ReleaseConfiguration conf, String writeTokenEnvValue) {
        String ghUser = conf.getGitHub().getWriteAuthUser();
        String ghRepo = conf.getGitHub().getRepository();
        String writeToken = getWriteToken(conf, writeTokenEnvValue);
        setPushUrl(pushTask, writeTokenEnvValue, ghUser, ghRepo, writeToken);
    }

    static void setPushUrl(GitPushTask pushTask, String writeTokenEnvValue, String ghUser, String ghRepo, String writeToken) {
        if (writeToken != null) {
            String url = MessageFormat.format("https://{0}:{1}@github.com/{2}.git", ghUser, writeTokenEnvValue, ghRepo);
            pushTask.setUrl(url);
            pushTask.setSecretValue(writeToken);
        } else {
            LOG.lifecycle("  'git push' does not use GitHub write token because it was not specified.");
            String url = MessageFormat.format("https://github.com/{0}.git", ghRepo);
            pushTask.setUrl(url);
        }
    }

    public static String getWriteToken(ReleaseConfiguration conf, String writeTokenEnvValue) {
        String token = conf.getLenient().getGitHub().getWriteAuthToken();
        if (token != null) {
            LOG.lifecycle("  'git push' uses GitHub write token specified in shipkit configuration.");
            return token;
        }
        if (writeTokenEnvValue != null) {
            LOG.lifecycle("  'git push' uses GitHub write token specified by {} env variable.", "GH_WRITE_TOKEN");
            return writeTokenEnvValue;
        }
        return null;
    }

    public void gitPush(GitPushTask task) {
        new DefaultProcessRunner(task.getProject().getProjectDir())
                .setSecretValue(task.getSecretValue())
                .run(GitPush.gitPushArgs(task.getUrl(), task.getTargets(), task.isDryRun()));
    }
}

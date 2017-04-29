package org.mockito.release.internal.gradle.util;

import org.gradle.api.Project;
import org.mockito.release.gradle.ReleaseConfiguration;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Git utilities
 */
public class GitUtil {
    //TODO unit testable

    /**
     * Quiet command line to be used to perform git push without exposing write token
     */
    public static List<String> getGitPushArgs(ReleaseConfiguration conf, Project project) {
        String ghUser = conf.getGitHub().getWriteAuthUser();
        String ghWriteToken = conf.getGitHub().getWriteAuthToken();
        String ghRepo = conf.getGitHub().getRepository();
        String branch = conf.getBuild().getBranch();
        String url = MessageFormat.format("https://{0}:{1}@github.com/{2}.git", ghUser, ghWriteToken, ghRepo);

        ArrayList<String> args = new ArrayList<String>(asList("git", "push", url, branch, getTag(conf, project)));
        if (conf.isDryRun()) {
            args.add("--dry-run");
        }
        return args;
    }

    /**
     * Returns Git generic user notation based on settings, for example:
     * "Mockito Release Tools &lt;mockito.release.tools@gmail.com&gt;"
     */
    public static Object getGitGenericUserNotation(ReleaseConfiguration conf) {
        return conf.getGit().getUser() + " <" + conf.getGit().getEmail() + ">";
    }

    /**
     * Returns Git tag based on release configuration and project version
     */
    public static String getTag(ReleaseConfiguration conf, Project project) {
        return conf.getGit().getTagPrefix() + project.getVersion();
    }

    /**
     * Returns Git commit message based on release configuration and the given message
     */
    public static String getCommitMessage(ReleaseConfiguration conf, String message) {
        String postfix = conf.getGit().getCommitMessagePostfix();
        if (postfix.isEmpty()) {
            return message;
        }
        return message + " " + postfix;
    }
}

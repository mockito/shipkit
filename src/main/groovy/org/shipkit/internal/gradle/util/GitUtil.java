package org.shipkit.internal.gradle.util;

import org.gradle.api.Project;
import org.shipkit.gradle.configuration.ShipkitConfiguration;

/**
 * Git utilities
 */
public class GitUtil {

    /**
     * Returns Git generic user notation based on settings, for example:
     * "Mockito Release Tools &lt;mockito.release.tools@gmail.com&gt;"
     */
    public static String getGitGenericUserNotation(String gitUserName, String gitUserEmail) {
        return gitUserName + " <" + gitUserEmail + ">";
    }

    /**
     * Returns Git tag based on release configuration and project version
     */
    public static String getTag(ShipkitConfiguration conf, Project project) {
        return conf.getGit().getTagPrefix() + project.getVersion();
    }

    /**
     * Returns Git commit message based on release configuration and the given message
     */
    public static String getCommitMessage(String message, String commitMessagePostfix) {
        if (commitMessagePostfix.isEmpty()) {
            return message;
        }
        return message + " " + commitMessagePostfix;
    }
}

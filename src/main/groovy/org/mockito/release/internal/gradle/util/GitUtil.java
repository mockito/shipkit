package org.mockito.release.internal.gradle.util;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.mockito.release.gradle.ReleaseConfiguration;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * Git utilities
 */
public class GitUtil {
    //TODO unit testable

    private final static Logger LOG = Logging.getLogger(GitUtil.class);

    /**
     * Quiet command line to be used to perform git push without exposing write token
     */
    public static Collection<String> getQuietGitPushArgs(ReleaseConfiguration conf, Project project) {
        //TODO push everyting on conf object, get rid of ext
        //!!!Below command _MUST_ be quiet otherwise it exposes GitHub write token!!!
        String mustBeQuiet = "-q";
        String ghUser = conf.getGitHub().getWriteAuthUser();
        String ghWriteToken = conf.getGitHub().getWriteAuthToken();
        String ghRepo = conf.getGitHub().getRepository();
        String branch = conf.getGit().getBranch();
        String url = MessageFormat.format("https://{0}:[SECRET]@github.com/{1}.git", ghUser, ghRepo);

        ArrayList<String> args = new ArrayList<String>(asList("git", "push", url, branch, getTag(conf, project), mustBeQuiet));
        if (conf.isDryRun()) {
            args.add("--dry-run");
        }
        //TODO git push arguments should be printed when task runs and not when the args are configured on the task
        LOG.lifecycle("  'git push' arguments:\n    {}", StringUtil.join(args, " "));
        //!!! Setting the url after printing the command so that we don't expose the sensitive token!!!
        String actualUrl = args.get(2).replace("[GH_WRITE_TOKEN]", ghWriteToken);
        args.set(2, actualUrl);
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
}

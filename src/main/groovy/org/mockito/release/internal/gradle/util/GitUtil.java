package org.mockito.release.internal.gradle.util;

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

    private final static Logger LOG = Logging.getLogger(GitUtil.class);

    /**
     * Quiet command line to be used to perform git push without exposing write token
     */
    public static Collection<String> getQuietGitPushArgs(ReleaseConfiguration conf, ExtContainer ext) {
        //TODO push everyting on conf object, get rid of ext
        //TODO unit testable
        //!!!Below command _MUST_ be quiet otherwise it exposes GitHub write token!!!
        String mustBeQuiet = "-q";
        String ghUser = ext.getString("gh_user");
        String ghWriteToken = conf.getGitHub().getWriteAuthToken();
        String ghRepo = conf.getGitHub().getRepository();
        String branch = conf.getGit().getBranch();
        String url = MessageFormat.format("https://{0}:[GH_WRITE_TOKEN]@github.com/{1}.git", ghUser, ghRepo);

        ArrayList<String> args = new ArrayList<String>(asList("git", "push", url, branch, ext.getTag(), mustBeQuiet));
        if (conf.isDryRun()) {
            args.add("--dry-run");
        }
        LOG.lifecycle("  'git push' arguments:\n    {}", StringUtil.join(args, " "));
        //!!! Setting the url after printing the command so that we don't expose the sensitive token!!!
        String actualUrl = args.get(2).replace("[GH_WRITE_TOKEN]", ghWriteToken);
        args.set(2, actualUrl);
        return args;

    }
}

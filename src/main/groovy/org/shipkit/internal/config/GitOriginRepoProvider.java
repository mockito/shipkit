package org.shipkit.internal.config;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.exec.ProcessRunner;

public class GitOriginRepoProvider {

    private static final Logger LOG = Logging.getLogger(GitOriginRepoProvider.class);
    public static final String FALLBACK_REPO = "mockito/mockito-release-tools-example";
    private final ProcessRunner processRunner;

    public GitOriginRepoProvider(ProcessRunner processRunner){
        this.processRunner = processRunner;
    }

    /**
     * fetches remote url for git origin and returns it in format "user/repo", eg. "mockito/mockito-release-tools"
     * in case "git remote" call fails it returns {#FALLBACK_REPO}
     */
    public String getOriginGitRepo(){
        try {
            return processRunner.run("git", "remote", "get-url", "origin") // for GitHub returns git@github.com:user/repo.git
                    .trim()
                    .replaceFirst("^.*?:", "") // remove everything before ':'
                    .replace(".git", ""); // remove .git suffix
        } catch(Exception e){
            LOG.error("Failed to get url of git remote origin", e);
            return FALLBACK_REPO;
        }
    }
}

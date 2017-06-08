package org.shipkit.internal.notes.vcs;

import org.shipkit.internal.exec.ProcessRunner;

public class GitOriginRepoProvider {

    private final ProcessRunner processRunner;

    public GitOriginRepoProvider(ProcessRunner processRunner){
        this.processRunner = processRunner;
    }

    /**
     * fetches remote url for git origin and returns it in format "user/repo", eg. "mockito/shipkit"
     */
    public String getOriginGitRepo(){
        return processRunner.run("git", "remote", "get-url", "origin") // for GitHub returns git@github.com:user/repo.git
                    .trim()
                    .replaceFirst("^.*?:", "") // remove everything before ':'
                    .replaceFirst(".git$", ""); // remove .git suffix
    }
}

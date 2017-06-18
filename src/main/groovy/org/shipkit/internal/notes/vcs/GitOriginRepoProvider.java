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
        // for GitHub returns git@github.com:user/repo.git or https://github.com/user/repo.git
        String remote = processRunner.run("git", "remote", "get-url", "origin").trim();

        if(remote.startsWith("git")){
            return remote
                    .replaceFirst("^.*?:", "") // remove everything before ':'
                    .replaceFirst(".git$", ""); // remove .git suffix
        } else{
            return remote
                    .replaceFirst("^http(s?):\\/\\/.*?\\/", "") // remove protocol and domain
                    .replaceFirst(".git$", ""); // remove .git suffix
        }
    }
}

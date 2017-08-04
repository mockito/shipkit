package org.shipkit.internal.gradle.git.tasks;

import org.shipkit.internal.exec.ProcessRunner;

import java.util.Arrays;
import java.util.List;

class GitOriginRepoProvider {

    private final ProcessRunner processRunner;

    public GitOriginRepoProvider(ProcessRunner processRunner){
        this.processRunner = processRunner;
    }

    /**
     * fetches remote url for git origin and returns it in format "user/repo", eg. "mockito/shipkit"
     */
    public String getOriginGitRepo(){
        // for GitHub returns git@github.com:user/repo.git or https://github.com/user/repo.git
        List<String> command = Arrays.asList("git", "remote", "get-url", "origin");
        String output = processRunner.run(command).trim();

        if(output.startsWith("git")){
            return output
                    .replaceFirst("^.*?:", "") // remove everything before ':'
                    .replaceFirst(".git$", ""); // remove .git suffix
        } else {
            return output
                .replaceFirst("^http(s?):\\/\\/.*?\\/", "") // remove protocol and domain
                .replaceFirst(".git$", ""); // remove .git suffix
        }
    }
}

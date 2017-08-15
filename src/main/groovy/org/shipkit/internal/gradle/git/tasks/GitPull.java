package org.shipkit.internal.gradle.git.tasks;

import org.shipkit.internal.exec.DefaultProcessRunner;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for configuring git pull task with the correct git pull arguments.
 */
public class GitPull {

    public void gitPull(GitPullTask task){

        new DefaultProcessRunner(task.getProject().getProjectDir())
            .setSecretValue(task.getSecretValue())
            .run(gitPullArgs(task.getUrl(), task.getRev(), task.isDryRun()));
    }

    /**
     * Constructs git pull arguments basing on the url, rev and dry run
     */
    static List<String> gitPullArgs(String url, String rev, boolean dryRun) {
        List<String> args = new LinkedList<String>();
        args.add("git");
        args.add("pull");
        args.add(url);
        args.add(rev);
        if (dryRun) {
            args.add("--dry-run");
        }
        return args;
    }
}

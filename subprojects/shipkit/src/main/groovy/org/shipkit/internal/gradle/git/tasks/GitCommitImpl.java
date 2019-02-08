package org.shipkit.internal.gradle.git.tasks;

import org.shipkit.gradle.exec.ExecCommand;
import org.shipkit.gradle.git.GitCommitTask;
import org.shipkit.internal.gradle.exec.ShipkitExec;
import org.shipkit.internal.gradle.util.GitUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.shipkit.internal.gradle.exec.ExecCommandFactory.execCommand;

public class GitCommitImpl {

    public void commit(GitCommitTask task) {
        Collection<ExecCommand> commands = new LinkedList<>();
        commands.add(execCommand("Adding files to git",
            getAddCommand(task.getFilesToCommit(), task.getDirectoriesToCommit())));
        commands.add(execCommand("Performing git commit",
            getCommitCommand(task.getGitUserName(), task.getGitUserEmail(), task.getDescriptions(), task.getCommitMessagePostfix())));
        new ShipkitExec().execCommands(commands, task.getProject(), task.getWorkingDir());
    }

    static String getAggregatedCommitMessage(List<String> descriptions) {
        StringBuilder result = new StringBuilder();
        for (String msg : descriptions) {
            result.append(msg).append(" + ");
        }
        if (!descriptions.isEmpty()) {
            result.delete(result.length() - 3, result.length());
        }
        return result.toString();
    }

    static List<String> getAddCommand(List<File> files, List<File> directories) {
        List<String> args = new ArrayList<>();
        args.add("git");
        args.add("add");
        for (File file : files) {
            args.add(file.getAbsolutePath());
        }
        for (File directory : directories) {
            args.add(directory.getAbsolutePath());
        }
        return args;
    }

    static List<String> getCommitCommand(String gitUserName, String gitUserEmail,
                                         List<String> descriptions, String commitMessagePostfix) {
        List<String> args = new ArrayList<>();
        args.add("git");
        args.add("commit");
        args.add("--author");
        args.add(GitUtil.getGitGenericUserNotation(gitUserName, gitUserEmail));
        args.add("-m");
        args.add(GitUtil.getCommitMessage(getAggregatedCommitMessage(descriptions), commitMessagePostfix));
        return args;
    }
}

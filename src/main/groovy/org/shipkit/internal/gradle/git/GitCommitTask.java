package org.shipkit.internal.gradle.git;

import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.gradle.exec.ExecCommand;
import org.shipkit.internal.gradle.exec.ShipkitExec;
import org.shipkit.internal.gradle.util.GitUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.shipkit.internal.gradle.exec.ExecCommandFactory.execCommand;

/**
 * Commits all changes registered with {@link GitCommitTask#addChange} method
 * Commit message is concatenated from all descriptions of registered changes
 */
public class GitCommitTask extends DefaultTask {

    @Input @SkipWhenEmpty private List<File> filesToCommit = new ArrayList<File>();
    private List<String> descriptions = new ArrayList<String>();

    @Input String gitUserName;
    @Input String gitUserEmail;
    @Input String commitMessagePostfix;

    public void addChange(List<File> files, String changeDescription, Task taskMakingChange) {
        dependsOn(taskMakingChange);
        filesToCommit.addAll(files);
        descriptions.add(changeDescription);
    }

    public String getGitUserName() {
        return gitUserName;
    }

    public void setGitUserName(String gitUserName) {
        this.gitUserName = gitUserName;
    }

    public String getGitUserEmail() {
        return gitUserEmail;
    }

    public void setGitUserEmail(String gitUserEmail) {
        this.gitUserEmail = gitUserEmail;
    }

    public String getCommitMessagePostfix() {
        return commitMessagePostfix;
    }

    public void setCommitMessagePostfix(String commitMessagePostfix) {
        this.commitMessagePostfix = commitMessagePostfix;
    }

    @TaskAction public void commit() {
        Collection<ExecCommand> commands = new LinkedList<ExecCommand>();
        commands.add(execCommand("Adding files to git", getAddCommand(getFiles())));
        commands.add(execCommand("Performing git commit", getCommitCommand(getAggregatedCommitMessage())));
        new ShipkitExec().execCommands(commands, getProject());
    }

    public List<String> getFiles() {
        List<String> result = new ArrayList<String>();
        for (File file : filesToCommit) {
            result.add(file.getAbsolutePath());
        }
        return result;
    }

    public String getAggregatedCommitMessage() {
        StringBuilder result = new StringBuilder();
        for (String msg : descriptions) {
            result.append(msg).append(" + ");
        }
        if (!descriptions.isEmpty()) {
            result.delete(result.length() - 3, result.length());
        }
        return result.toString();
    }

    private List<String> getAddCommand(List<String> files) {
        List<String> args = new ArrayList<String>();
        args.add("git");
        args.add("add");
        args.addAll(files);
        return args;
    }

    private List<String> getCommitCommand(String aggregatedCommitMsg) {
        List<String> args = new ArrayList<String>();
        args.add("git");
        args.add("commit");
        args.add("--author");
        args.add(GitUtil.getGitGenericUserNotation(this.gitUserName, this.gitUserEmail));
        args.add("-m");
        args.add(GitUtil.getCommitMessage(aggregatedCommitMsg, this.commitMessagePostfix));
        return args;
    }
}

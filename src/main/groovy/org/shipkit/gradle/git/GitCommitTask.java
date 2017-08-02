package org.shipkit.gradle.git;

import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.git.tasks.GitCommitImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    @TaskAction public void commit() {
        new GitCommitImpl().commit(this);
    }

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

    public List<File> getFilesToCommit() {
        return filesToCommit;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }
}

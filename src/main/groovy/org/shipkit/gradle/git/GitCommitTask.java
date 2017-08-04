package org.shipkit.gradle.git;

import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
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

    /**
     * Registers a change to be committed.
     * Invoke this method only in Gradle's configuration phase because this method calls 'dependsOn' automatically.
     *
     * @param files to be committed
     * @param changeDescription description to be included in commit message
     * @param taskMakingChange task that makes the change, we will automatically set 'dependsOn' this task
     */
    public void addChange(List<File> files, String changeDescription, Task taskMakingChange) {
        dependsOn(taskMakingChange);
        filesToCommit.addAll(files);
        descriptions.add(changeDescription);
    }

    /**
     * See {@link ShipkitConfiguration.Git#getUser()}
     */
    public String getGitUserName() {
        return gitUserName;
    }

    /**
     * See {@link #getGitUserName()}
     */
    public void setGitUserName(String gitUserName) {
        this.gitUserName = gitUserName;
    }

    /**
     * See {@link ShipkitConfiguration.Git#getEmail()}
     */
    public String getGitUserEmail() {
        return gitUserEmail;
    }

    /**
     * See {@link #getGitUserEmail()}
     */
    public void setGitUserEmail(String gitUserEmail) {
        this.gitUserEmail = gitUserEmail;
    }

    /**
     * See {@link ShipkitConfiguration.Git#getCommitMessagePostfix()}
     */
    public String getCommitMessagePostfix() {
        return commitMessagePostfix;
    }

    /**
     * See {@link #getCommitMessagePostfix()}
     */
    public void setCommitMessagePostfix(String commitMessagePostfix) {
        this.commitMessagePostfix = commitMessagePostfix;
    }

    /**
     * The files to be committed as registered using {@link #addChange(List, String, Task)} method.
     */
    public List<File> getFilesToCommit() {
        return filesToCommit;
    }

    /**
     * Change descriptions to be included in the commit message.
     * Descriptions are registered using {@link #addChange(List, String, Task)}
     */
    public List<String> getDescriptions() {
        return descriptions;
    }
}

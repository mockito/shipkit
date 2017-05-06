package org.mockito.release.internal.gradle;


import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.exec.DefaultProcessRunner;

/**
 * This task will checkout a certain revision.
 */
public class GitCheckOutTask extends DefaultTask {

    @Input
    private String rev;

    public void setRev(String rev) {
        this.rev = rev;
    }

    @TaskAction
    public void checkOut() {
        new DefaultProcessRunner(getProject().getProjectDir()).run("git", "checkout", rev);
    }
}

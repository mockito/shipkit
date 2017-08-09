package org.shipkit.internal.gradle.git;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

/**
 * Performs git pull operation.
 * It does not expose the command line parameters when build is executed with '-i' (--info) level.
 * It masks secret value configured via {@link #setSecretValue(String)} from logging, task output and exception messages.
 * Replaces secret value with "[SECRET]".
 * It really helps debugging if we can see the output and logging without exposing secret values like GitHub write auth token.
 */
public class GitPullTask extends DefaultTask{

    @Input private String url;
    @Input private String rev;
    @Input private boolean dryRun;
    private String secretValue;

    @TaskAction
    public void gitPull(){
        new GitPull().gitPull(this);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public String getSecretValue() {
        return secretValue;
    }

    public void setSecretValue(String secretValue) {
        this.secretValue = secretValue;
    }
}

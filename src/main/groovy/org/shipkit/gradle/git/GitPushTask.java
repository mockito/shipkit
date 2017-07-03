package org.shipkit.gradle.git;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.git.GitPush;

import java.util.LinkedList;
import java.util.List;

/**
 * Performs git push operation.
 * It does not expose the command line parameters when build is executed with '-i' (--info) level.
 * It masks secret value configured via {@link #setSecretValue(String)} from logging, task output and exception messages.
 * Replaces secret value with "[SECRET]".
 * It really helps debugging if we can see the output and logging without exposing secret values like GitHub write auth token.
 */
public class GitPushTask extends DefaultTask {

    @Input private List<String> targets = new LinkedList<String>();
    @Input private String url;
    @Input private boolean dryRun;
    private String secretValue;

    @TaskAction public void gitPush() {
        new GitPush().gitPush(this);
    }

    /**
     * Targets of the git push (e.g. branches, tags)
     */
    public List<String> getTargets() {
        return targets;
    }

    /**
     * See {@link #getTargets()}
     */
    public void setTargets(List<String> targets) {
        this.targets = targets;
    }

    /**
     * Git push url, may contain unmasked write auth token so don't log out this information!
     */
    public String getUrl() {
        return url;
    }

    /**
     * See {@link #getUrl()}
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Whether git push should be run with dry run
     */
    public boolean isDryRun() {
        return dryRun;
    }

    /**
     * See {@link #isDryRun()}
     */
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * Value to be secured, e.g. masked from forked process output and logging.
     * Used for masking github write token.
     */
    public String getSecretValue() {
        return secretValue;
    }

    /**
     * See {@link #getSecretValue()}
     */
    public void setSecretValue(String secretValue) {
        this.secretValue = secretValue;
    }
}

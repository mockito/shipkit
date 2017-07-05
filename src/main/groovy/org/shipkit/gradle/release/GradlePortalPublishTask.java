package org.shipkit.gradle.release;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.gradle.exec.ShipkitExecTask;
import org.shipkit.internal.gradle.release.tasks.GradlePortalPublish;

//TODO javadoc
public class GradlePortalPublishTask extends ShipkitExecTask {

    @Input String publishKey;
    @Input String publishSecret;
    boolean dryRun;

    @TaskAction public void publishPlugins() {
        new GradlePortalPublish().publishPlugins(this);
    }

    public String getPublishKey() {
        return publishKey;
    }

    public void setPublishKey(String publishKey) {
        this.publishKey = publishKey;
    }

    public String getPublishSecret() {
        return publishSecret;
    }

    public void setPublishSecret(String publishSecret) {
        this.publishSecret = publishSecret;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }
}

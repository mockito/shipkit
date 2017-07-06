package org.shipkit.gradle.release;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.gradle.exec.ShipkitExecTask;
import org.shipkit.internal.gradle.release.tasks.GradlePortalPublish;

/**
 * Publishes to Gradle Plugin portal (http://plugins.gradle.org).
 * Wraps the Gradle's official 'publishPlugins' task
 * so that we can conveniently configure publish key and publish secret with env variables.
 * This is needed for CI workflows and continuous delivery.
 */
public class GradlePortalPublishTask extends ShipkitExecTask {

    @Input String publishKey;
    @Input String publishSecret;

    @TaskAction public void publishPlugins() {
        new GradlePortalPublish().publishPlugins(this);
    }

    /**
     * Publish key as required by Gradle Plugin portal (http://plugins.gradle.org)
     */
    public String getPublishKey() {
        return publishKey;
    }

    /**
     * See {@link #getPublishKey()}
     */
    public void setPublishKey(String publishKey) {
        this.publishKey = publishKey;
    }

    /**
     * Publish secret as required by Gradle Plugin portal (http://plugins.gradle.org)
     */
    public String getPublishSecret() {
        return publishSecret;
    }

    /**
     * See {@link #getPublishSecret()}
     */
    public void setPublishSecret(String publishSecret) {
        this.publishSecret = publishSecret;
    }
}

package org.shipkit.gradle.configuration;

import org.gradle.api.GradleException;

public class AndroidPublishConfiguration {

    private String artifactId;

    /**
     * Artifact id of published AAR
     * For example: "shipkit-android"
     */
    public String getArtifactId() {
        if (artifactId == null || artifactId.isEmpty()) {
            throw new GradleException("Please configure artifact id");
        }
        return artifactId;
    }

    /**
     * See {@link #getArtifactId()} ()}
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
}

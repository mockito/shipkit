package org.shipkit.gradle.configuration;

import static org.shipkit.internal.util.ArgumentValidation.notNull;

public class AndroidLibraryPublishConfiguration {

    private String artifactId;

    /**
     * Artifact id of published AAR
     * For example: "shipkit-android"
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * See {@link #getArtifactId()} ()}
     */
    public void setArtifactId(String artifactId) {
        notNull(artifactId, "artifactId");
        this.artifactId = artifactId;
    }
}

package org.shipkit.internal.comparison.artifact;

public class GradlePluginArtifactUrlResolver implements DefaultArtifactUrlResolver {

    /**
     * Group of the project, eg. "org.shipkit"
     */
    private String projectGroup;
    /**
     * Name of the artifact, usually the same as the name of jar without the version suffix,
     * eg. if jar is named "shipkit-0.4.3.jar", baseName is "shipkit"
     */
    private String artifactBaseName;
    /**
     * Version of the artifact
     */
    private String version;

    public GradlePluginArtifactUrlResolver(String projectGroup, String artifactBaseName, String version) {
        this.projectGroup = projectGroup;
        this.artifactBaseName = artifactBaseName;
        this.version = version;
    }

    /**
     * Returns gradle plugin URL, in format eg.:
     * https://plugins.gradle.org/m2/org/shipkit/shipkit/0.9.84/shipkit-0.9.84.pom
     */
    @Override
    public String getDefaultUrl(String extension) {
        return String.format("https://plugins.gradle.org/m2/%s/%s/%s/%s-%s%s",
            projectGroup.replace('.', '/'),
            artifactBaseName,
            version,
            artifactBaseName,
            version,
            extension);
    }
}

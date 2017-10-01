package org.shipkit.internal.comparison.artifact;

import org.gradle.api.Project;
import org.shipkit.internal.gradle.bintray.ShipkitBintrayPlugin;
import org.shipkit.internal.gradle.plugin.GradlePortalPublishPlugin;

public class DefaultArtifactUrlResolverFactory {

    public DefaultArtifactUrlResolver getDefaultResolver(Project project, String artifactBaseName, String previousVersion) {
        if (project.getPlugins().hasPlugin(ShipkitBintrayPlugin.class)) {
            return new BintrayDefaultArtifactUrlResolver(project, artifactBaseName, previousVersion);
        } else if (project.getPlugins().hasPlugin(GradlePortalPublishPlugin.class)) {
            return new GradlePluginArtifactUrlResolver(project.getGroup().toString(), artifactBaseName, previousVersion);
        }
        return null;
    }

}

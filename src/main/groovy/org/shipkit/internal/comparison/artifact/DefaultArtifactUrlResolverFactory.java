package org.shipkit.internal.comparison.artifact;

import org.gradle.api.Project;
import org.shipkit.internal.gradle.ShipkitBintrayPlugin;

public class DefaultArtifactUrlResolverFactory {

    public DefaultArtifactUrlResolver getDefaultResolver(Project project, String artifactBaseName, String previousVersion){
        if(project.getPlugins().hasPlugin(ShipkitBintrayPlugin.class)){
            return new BintrayDefaultArtifactUrlResolver(project, artifactBaseName, previousVersion);
        }
        return null;
    }

}

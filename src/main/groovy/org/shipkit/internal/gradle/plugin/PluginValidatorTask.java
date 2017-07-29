package org.shipkit.internal.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.Set;


public class PluginValidatorTask extends DefaultTask {

    @Optional
    @Input
    private SourceSet sourceSet;

    @TaskAction
    public void validate() {
        Set<File> gradlePlugins;
        Set<File> gradleProperties;
        if (sourceSet == null) {
            gradlePlugins = PluginUtil.discoverGradlePlugins(getProject());
            gradleProperties = PluginUtil.discoverGradlePluginPropertyFiles(getProject());
        } else {
            gradlePlugins = PluginUtil.discoverGradlePlugins(sourceSet);
            gradleProperties = PluginUtil.discoverGradlePluginPropertyFiles(sourceSet);
        }
        new PluginValidator().validate(gradlePlugins, gradleProperties);
    }

    public void setSourceSet(SourceSet sourceSet) {
        this.sourceSet = sourceSet;
    }

    public SourceSet getSourceSet() {
        return sourceSet;
    }
}

package org.shipkit.internal.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Set;


public class PluginValidatorTask extends DefaultTask {

    @InputFiles
    private Set<File> gradlePlugins;
    @InputFiles
    private Set<File> gradleProperties;


    @TaskAction
    public void validate() {
        new PluginValidator().validate(gradlePlugins, gradleProperties);
    }

    public void setGradlePlugins(Set<File> gradlePlugins) {
        this.gradlePlugins = gradlePlugins;
    }

    public Set<File> getGradlePlugins() {
        return gradlePlugins;
    }

    public void setGradleProperties(Set<File> gradleProperties) {
        this.gradleProperties = gradleProperties;
    }

    public Set<File> getGradleProperties() {
        return gradleProperties;
    }
}

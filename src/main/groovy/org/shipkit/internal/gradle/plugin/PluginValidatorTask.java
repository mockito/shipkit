package org.shipkit.internal.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.Set;

/**
 * This task validates plugin properties files.
 * The following constraints are validated:
 * <ul>
 *     <li>plugins have a corresponding .properties file</li>
 *     <li>the name of the properties file is consistent with the class name (e.g BintrayReleasePlugin.java" -> "org.shipkit.bintray-release")</li>
 * </ul>
 */
public class PluginValidatorTask extends DefaultTask {

    @Input
    private SourceSet sourceSet;

    @TaskAction
    public void validate() {
        Set<File> gradlePlugins = PluginUtil.discoverGradlePlugins(sourceSet);
        Set<File> gradleProperties = PluginUtil.discoverGradlePluginPropertyFiles(sourceSet);
        new PluginValidator().validate(gradlePlugins, gradleProperties);
    }

    public void setSourceSet(SourceSet sourceSet) {
        this.sourceSet = sourceSet;
    }

    public SourceSet getSourceSet() {
        return sourceSet;
    }
}

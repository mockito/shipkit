package org.shipkit.internal.gradle.plugin.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.util.IncubatingWarning;

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

    @TaskAction
    public void validate() {
        new PluginValidator().validate(getProject());
    }
}

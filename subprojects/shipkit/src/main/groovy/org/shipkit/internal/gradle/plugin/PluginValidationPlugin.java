package org.shipkit.internal.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.plugin.PluginValidatorTask;
import org.shipkit.internal.gradle.util.JavaPluginUtil;
import org.shipkit.internal.gradle.util.TaskMaker;

/**
 * This plugin validates that every plugin has a corresponding .properties file.
 *
 * Adds tasks:
 * <ul>
 *     <li>'validatePlugins' - of type {@link PluginValidatorTask}.
 *          Validates that every plugin has a corresponding .properties file.</li>
 * </ul>
 */
public class PluginValidationPlugin implements Plugin<Project> {

    static final String VALIDATE_PLUGINS = "validatePlugins";

    @Override
    public void apply(final Project project) {
        project.getPlugins().withId("java", plugin -> TaskMaker.task(project, VALIDATE_PLUGINS, PluginValidatorTask.class, task -> {
            task.setDescription("Validates Gradle Plugins and their properties files");
            task.setSourceSet(JavaPluginUtil.getMainSourceSet(project));
        }));
    }
}

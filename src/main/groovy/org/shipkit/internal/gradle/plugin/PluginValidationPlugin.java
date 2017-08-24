package org.shipkit.internal.gradle.plugin;


import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.plugin.tasks.PluginValidatorTask;
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
        project.getPlugins().withId("java", new Action<Plugin>() {
            @Override
            public void execute(Plugin plugin) {
                TaskMaker.task(project, VALIDATE_PLUGINS, PluginValidatorTask.class, new Action<PluginValidatorTask>() {
                    @Override
                    public void execute(PluginValidatorTask task) {
                        task.setDescription("Validates Gradle Plugins and their properties files");
                    }
                });
            }
        });
    }
}

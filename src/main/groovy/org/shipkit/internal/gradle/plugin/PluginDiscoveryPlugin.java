package org.shipkit.internal.gradle.plugin;

import com.gradle.publish.PluginBundleExtension;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.plugin.PluginDiscoveryTask;
import org.shipkit.internal.gradle.util.TaskMaker;

import static org.shipkit.internal.gradle.release.GradlePortalReleasePlugin.PUBLISH_PLUGINS_TASK;

/**
 * This plugin discovers gradle plugins and adds them to the {@link PluginBundleExtension}.
 *
 * Maintaining plugins manually is error-prone. E.g. someone might easily forget about adding a new plugin. This plugin
 * will automatically pick up available gradle plugins (discovered via properties files in META-INF/gradle-plugins) and
 * will configure the pluginBundle extension (provided via 'com.gradle.plugin-publish' plugin) accordingly.
 *
 * Adds tasks:
 * <ul>
 *     <li>'discoverPlugins' - of type {@link PluginDiscoveryTask}.
 *          Discovers gradle plugins and configures the pluginBundle extension accordingly.</li>
 * </ul>
 */
public class PluginDiscoveryPlugin implements Plugin<Project> {

    static final String DISCOVER_PLUGINS = "discoverPlugins";

    @Override
    public void apply(final Project project) {
        project.getPlugins().withId("com.gradle.plugin-publish", new Action<Plugin>() {

            @Override
            public void execute(final Plugin plugin) {
                final Task task = TaskMaker.task(project, DISCOVER_PLUGINS, PluginDiscoveryTask.class, new Action<PluginDiscoveryTask>() {
                    @Override
                    public void execute(final PluginDiscoveryTask task) {
                        task.setDescription("discover gradle plugins");
                    }
                });

                final Task publishPlugins = project.getTasks().getByName(PUBLISH_PLUGINS_TASK);
                publishPlugins.dependsOn(task);
            }

        });
    }

}

package org.shipkit.internal.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.AutoVersioningPlugin;
import org.shipkit.internal.gradle.TravisPlugin;

/**
 * Plugin contains everything you need to automatically bump your version in Travis CI environment
 *
 * <ul>
 *     <li>{@link AutoVersioningPlugin}</li>
 *     <li>{@link TravisPlugin}</li>
 *     <li>{@link PluginDiscoveryPlugin}</li>
 * </ul>
 *
 * Adds following tasks:
 *
 * <ul>
 *     <li></li>
 * </ul>
 */
public class ShipkitGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(AutoVersioningPlugin.class);
        project.getPlugins().apply(TravisPlugin.class);
        project.getPlugins().apply(PluginDiscoveryPlugin.class);
    }
}

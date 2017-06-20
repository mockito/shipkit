package org.shipkit.internal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Plugin contains everything you need to automatically bump your version in Travis CI environment
 *
 * <ul>
 *     <li>{@link AutoVersioningPlugin}</li>
 *     <li>{@link TravisPlugin}</li>
 *     <li>{@link AutoDiscoverGradlePluginsPlugin}</li>
 * </ul>
 *
 * Adds following tasks:
 *
 * <ul>
 *     <li></li>
 * </ul>
 */
public class GradlePluginReleasingPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(AutoVersioningPlugin.class);
        project.getPlugins().apply(TravisPlugin.class);
        project.getPlugins().apply(AutoDiscoverGradlePluginsPlugin.class);
    }
}

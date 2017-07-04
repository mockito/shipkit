package org.shipkit.internal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.internal.gradle.release.CiReleasePlugin;
import org.shipkit.internal.gradle.release.ReleasePlugin;
import org.shipkit.internal.gradle.util.TaskMaker;

/**
 * Plugin contains everything you need to automatically bump your version in Travis CI environment
 *
 * <ul>
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
public class GradlePluginReleasingPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(TravisPlugin.class);
        project.getPlugins().apply(PluginDiscoveryPlugin.class);
        project.getPlugins().apply(CiReleasePlugin.class);
        project.getPlugins().apply(GradlePluginReleasingPlugin.class);
    }
}

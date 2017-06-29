package org.shipkit.internal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.release.CiReleasePlugin;
import org.shipkit.internal.gradle.release.JavaReleasePlugin;

/**
 * Adds plugins and tasks to setup automated releasing for a typical Java project.
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link JavaReleasePlugin}</li>
 *     <li>{@link TravisPlugin}</li>
 *     <li>{@link CiReleasePlugin}</li>
 * </ul>
 */
public class ShipkitJavaPlugin implements Plugin<Project> {

    public void apply(final Project project) {
        project.getPlugins().apply(JavaReleasePlugin.class);
        project.getPlugins().apply(TravisPlugin.class);
        project.getPlugins().apply(CiReleasePlugin.class);
    }
}

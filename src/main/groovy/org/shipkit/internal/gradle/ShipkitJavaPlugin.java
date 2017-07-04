package org.shipkit.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.release.CiReleasePlugin;
import org.shipkit.internal.gradle.release.JavaReleasePlugin;

/**
 * Adds plugins and tasks to setup automated releasing for a typical Java project.
 * Will apply some configuration and plugins to all Java subprojects in a multi-project Gradle build.
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link JavaReleasePlugin}</li>
 *     <li>{@link TravisPlugin}</li>
 *     <li>{@link CiReleasePlugin}</li>
 * </ul>
 *
 * Applies following plugins to all subprojects that apply Gradle's "java" plugin:
 * <ul>
 *     <li>{@link JavaLibraryPlugin}</li>
 * </ul>
 */
public class ShipkitJavaPlugin implements Plugin<Project> {

    public void apply(final Project project) {
        project.getPlugins().apply(JavaReleasePlugin.class);
        project.getPlugins().apply(TravisPlugin.class);
        project.getPlugins().apply(CiReleasePlugin.class);

        project.allprojects(new Action<Project>() {
            public void execute(final Project subproject) {
                subproject.getPlugins().withId("java", new Action<Plugin>() {
                    @Override
                    public void execute(Plugin plugin) {
                        subproject.getPlugins().apply(JavaLibraryPlugin.class);
                    }
                });
            }
        });
    }
}

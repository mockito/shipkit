package org.shipkit.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.java.JavaBintrayPlugin;
import org.shipkit.internal.gradle.java.PomContributorsPlugin;
import org.shipkit.internal.gradle.release.BintrayReleasePlugin;

/**
 * Continuous delivery for Java with Travis and Bintray.
 * Intended for root project.
 * Adds plugins and tasks to setup automated releasing for a typical Java multi-project build.
 * Applies configuration and plugins to all Java subprojects in a multi-project Gradle build.
 * <p>
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link PomContributorsPlugin}</li>
 *     <li>{@link BintrayReleasePlugin}</li>
 *     <li>{@link TravisPlugin}</li>
 * </ul>
 */
public class ShipkitJavaPlugin implements Plugin<Project> {

    public void apply(final Project project) {
        project.getPlugins().apply(PomContributorsPlugin.class);
        project.getPlugins().apply(BintrayReleasePlugin.class);
        project.getPlugins().apply(TravisPlugin.class);

        project.allprojects(new Action<Project>() {
            public void execute(final Project subproject) {
                subproject.getPlugins().withId("java", new Action<Plugin>() {
                    @Override
                    public void execute(Plugin plugin) {
                        subproject.getPlugins().apply(JavaBintrayPlugin.class);
                    }
                });
            }
        });
    }
}

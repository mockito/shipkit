package org.shipkit.internal.gradle.java;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.GitHubPlugin;
import org.shipkit.internal.gradle.bintray.BintrayReleasePlugin;
import org.shipkit.internal.gradle.release.TravisPlugin;

/**
 * Continuous delivery for Java with Travis and Bintray.
 * Intended for root project of your Gradle project because it applies some configuration to 'allprojects'.
 * Adds plugins and tasks to setup automated releasing for a typical Java multi-project build.
 * <p>
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link PomContributorsPlugin}</li>
 *     <li>{@link BintrayReleasePlugin}</li>
 *     <li>{@link TravisPlugin}</li>
 * </ul>
 *
 * Adds behavior:
 * <ul>
 *     <li>Applies {@link JavaBintrayPlugin} to all Java projects in a multi-project Gradle build
 *          (all projects that use Gradle's "java" plugin).</li>
 * </ul>
 */
public class ShipkitJavaPlugin implements Plugin<Project> {

    public void apply(final Project project) {
        project.getPlugins().apply(GitHubPlugin.class);
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

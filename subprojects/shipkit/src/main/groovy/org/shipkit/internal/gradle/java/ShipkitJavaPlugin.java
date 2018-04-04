package org.shipkit.internal.gradle.java;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.release.ShipkitBasePlugin;
import org.shipkit.internal.gradle.util.ProjectUtil;

/**
 * Continuous delivery for Java with Travis and Bintray.
 * Intended for root project of your Gradle project because it applies some configuration to 'allprojects'.
 * Adds plugins and tasks to setup automated releasing for a typical Java multi-project build.
 * <p>
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link ShipkitBasePlugin}</li>
 *     <li>{@link PomContributorsPlugin}</li>
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
        ProjectUtil.requireRootProject(project, this.getClass());
        project.getPlugins().apply(ShipkitBasePlugin.class);
        project.getPlugins().apply(PomContributorsPlugin.class);

        project.allprojects(subproject -> subproject.getPlugins().withId("java", plugin -> {
            subproject.getPlugins().apply(JavaBintrayPlugin.class);
        }));
    }
}

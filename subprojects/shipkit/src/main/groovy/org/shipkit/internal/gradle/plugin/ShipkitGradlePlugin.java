package org.shipkit.internal.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.release.CiPlugin;
import org.shipkit.internal.gradle.release.GradlePortalReleasePlugin;
import org.shipkit.internal.gradle.release.TravisPlugin;
import org.shipkit.internal.gradle.util.ProjectUtil;

/**
 * Automatically ships your Gradle plugins to the Plugin Portal.
 * Intended for Gradle plugin authors who desire to release automatically, continually.
 * Intended for single-project builds that are Gradle-plugin projects.
 *
 * <ul>
 *     <li>{@link TravisPlugin}</li>
 *     <li>{@link GradlePortalReleasePlugin}</li>
 * </ul>
 */
public class ShipkitGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        ProjectUtil.requireRootProject(project, this.getClass());
        project.getPlugins().apply(CiPlugin.class);
        project.getPlugins().apply(GradlePortalReleasePlugin.class);
    }
}

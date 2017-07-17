package org.shipkit.internal.gradle.java;


import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.contributors.github.GithubContributorsPlugin;

/**
 * This plugin applies other plugins which make shipkit work with Github.
 * <p>
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link GithubContributorsPlugin}</li>
 *     <li>{@link PomContributorsPlugin}</li>
 * </ul>
 *
 */
public class GithubPomContributorsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(GithubContributorsPlugin.class);
        project.getPlugins().apply(PomContributorsPlugin.class);
    }
}

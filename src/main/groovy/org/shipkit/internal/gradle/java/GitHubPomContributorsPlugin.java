package org.shipkit.internal.gradle.java;


import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.contributors.github.GitHubContributorsPlugin;

/**
 * This plugin applies other plugins which make shipkit work with Github.
 * <p>
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link GitHubContributorsPlugin}</li>
 *     <li>{@link PomContributorsPlugin}</li>
 * </ul>
 *
 */
public class GitHubPomContributorsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(GitHubContributorsPlugin.class);
        project.getPlugins().apply(PomContributorsPlugin.class);
    }
}

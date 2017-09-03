package org.shipkit.internal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.contributors.github.GitHubContributorsPlugin;
import org.shipkit.internal.gradle.java.GitHubPomContributorsPlugin;

/**
 * This plugin applies other plugins which make shipkit work with Github.
 * Intended to be applied to the root project of your Gradle multi-project build.
 * <p>
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link GitHubContributorsPlugin}</li>
 *     <li>{@link GitHubPomContributorsPlugin}</li>
 * </ul>
 *
 */
public class GitHubPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(GitHubContributorsPlugin.class);
        project.getPlugins().apply(GitHubPomContributorsPlugin.class);
    }
}

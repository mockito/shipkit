package org.shipkit.internal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.contributors.github.GithubContributorsPlugin;
import org.shipkit.internal.gradle.java.GithubPomContributorsPlugin;

/**
 * This plugin applies other plugins which make shipkit work with Github.
 * Intended to be applied to the root project of your Gradle multi-project build.
 * <p>
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link GithubContributorsPlugin}</li>
 *     <li>{@link GithubPomContributorsPlugin}</li>
 * </ul>
 *
 */
public class GithubPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(GithubContributorsPlugin.class);
        project.getPlugins().apply(GithubPomContributorsPlugin.class);
    }
}

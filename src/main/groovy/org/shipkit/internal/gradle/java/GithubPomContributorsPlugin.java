package org.shipkit.internal.gradle.java;


import org.gradle.api.Project;
import org.shipkit.internal.gradle.contributors.github.GithubContributorsPlugin;

public class GithubPomContributorsPlugin extends PomContributorsPlugin {

    @Override
    void applyContributorsPlugin(Project project) {
        project.getPlugins().apply(GithubContributorsPlugin.class);
    }
}

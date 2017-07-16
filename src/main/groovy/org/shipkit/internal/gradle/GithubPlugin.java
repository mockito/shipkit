package org.shipkit.internal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.contributors.github.GithubContributorsPlugin;
import org.shipkit.internal.gradle.java.GithubPomContributorsPlugin;


public class GithubPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(GithubContributorsPlugin.class);
        project.getPlugins().apply(GithubPomContributorsPlugin.class);
    }
}

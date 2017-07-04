package org.shipkit.internal.gradle.release;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class GradlePortalReleasePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPlugins().apply(ReleasePlugin.class);

        Task performRelease = project.getTasks().getByName(ReleasePlugin.PERFORM_RELEASE_TASK);

        //need to apply Gradle's plugin portal releasing plugin, so that we have access to 'publishPlugins' task, then:
        performRelease.dependsOn("publishPlugins");

        /*

        Need a way to pass 'gradle.publish.key' and 'gradle.publish.secret' to 'publishPlugins' task
        This is how we currently do it in 'build.gradle' of shipkit project itself:

         commandLine "./gradlew", "publishPlugins", "performVersionBump",
                "-Pgradle.publish.key=${System.getenv('GRADLE_PUBLISH_KEY')}",
                "-Pgradle.publish.secret=${System.getenv('GRADLE_PUBLISH_SECRET')}"

         */
    }
}

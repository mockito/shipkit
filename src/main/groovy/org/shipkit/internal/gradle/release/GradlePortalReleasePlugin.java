package org.shipkit.internal.gradle.release;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.gradle.release.GradlePortalPublishTask;
import org.shipkit.internal.gradle.configuration.LazyConfiguration;
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;

//TODO SF javadoc
public class GradlePortalReleasePlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        project.getPlugins().apply(ReleasePlugin.class);
        project.getPlugins().apply("com.gradle.plugin-publish");

        TaskMaker.task(project, "performPublishPlugins", GradlePortalPublishTask.class, new Action<GradlePortalPublishTask>() {
            public void execute(final GradlePortalPublishTask t) {
                t.setDescription("Publishes to Gradle Plugin Portal by delegating to 'publishPlugins' task.");
                t.setDryRun(conf.isDryRun());
                t.setPublishKey(System.getenv("GRADLE_PUBLISH_KEY"));
                t.setPublishSecret(System.getenv("GRADLE_PUBLISH_SECRET"));
                LazyConfiguration.lazyConfiguration(t, new Runnable() {
                    @Override
                    public void run() {
                        //TODO log and validate secrets
                    }
                });

                Task performRelease = project.getTasks().getByName(ReleasePlugin.PERFORM_RELEASE_TASK);
                Task gitPush = project.getTasks().getByName(GitPlugin.GIT_PUSH_TASK);

                performRelease.dependsOn(t); //perform release will actually publish the plugins
                t.mustRunAfter(gitPush); //git push is easier to revers than perform release
                gitPush.mustRunAfter("buildArchives"); //run git push as late as possible
            }
        });
    }
}

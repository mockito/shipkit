package org.shipkit.internal.gradle.release;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.specs.Spec;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.gradle.release.GradlePortalPublishTask;
import org.shipkit.internal.gradle.configuration.BasicValidator;
import org.shipkit.internal.gradle.configuration.LazyConfiguration;
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.util.EnvVariables;

import javax.inject.Inject;

import static org.shipkit.internal.gradle.util.StringUtil.isEmpty;

//TODO SF javadoc
public class GradlePortalReleasePlugin implements Plugin<Project> {

    private final static Logger LOG = Logging.getLogger(GradlePortalReleasePlugin.class);

    final static String PUBLISH_KEY_ENV = "GRADLE_PUBLISH_KEY";
    final static String PUBLISH_SECRET_ENV = "GRADLE_PUBLISH_SECRET";
    final static String PERFORM_PUBLISH_TASK = "performPublishPlugins";
    private final EnvVariables envVariables;

    GradlePortalReleasePlugin(EnvVariables envVariables) {
        this.envVariables = envVariables;
    }

    @Inject public GradlePortalReleasePlugin() {
        this(new EnvVariables());
    }

    @Override
    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        project.getPlugins().apply(ReleasePlugin.class);
        project.getPlugins().apply("com.gradle.plugin-publish");

        TaskMaker.task(project, PERFORM_PUBLISH_TASK, GradlePortalPublishTask.class, new Action<GradlePortalPublishTask>() {
            public void execute(final GradlePortalPublishTask t) {
                t.setDescription("Publishes to Gradle Plugin Portal by delegating to 'publishPlugins' task.");
                t.setDryRun(conf.isDryRun());
                configureKey(t);
                configureSecret(t);
                LazyConfiguration.lazyConfiguration(t, new Runnable() {
                    @Override
                    public void run() {
                        validateSetting(t.getPublishKey(), "publishKey", PUBLISH_KEY_ENV, t);
                        validateSetting(t.getPublishSecret(), "publishSecret", PUBLISH_SECRET_ENV, t);
                    }
                });

                Task performRelease = project.getTasks().getByName(ReleasePlugin.PERFORM_RELEASE_TASK);
                Task gitPush = project.getTasks().getByName(GitPlugin.GIT_PUSH_TASK);

                performRelease.dependsOn(t); //perform release will actually publish the plugins
                t.mustRunAfter(gitPush); //git push is easier to revers than perform release
                gitPush.mustRunAfter("buildArchives"); //run git push as late as possible

                t.onlyIf(new Spec<Task>() {
                    @Override
                    public boolean isSatisfiedBy(Task t) {
                        if (conf.isDryRun()) {
                            LOG.info("dryRun is enabled, skipping '{}' using 'onlyIf'", t.getName());
                        }
                        return !conf.isDryRun();
                    }
                });
            }
        });
    }

    private static void validateSetting(String value, String settingName, String publishKeyEnv, GradlePortalPublishTask task) {
        BasicValidator.notNull(value, publishKeyEnv, "Gradle Plugin Portal '" + settingName + "' is required. " +
                "Export '" + publishKeyEnv + "' env var or configure '" + task.getName() + "' task.");
    }

    private void configureKey(GradlePortalPublishTask t) {
        Object key = t.getProject().findProperty("gradle.publish.key");
        if (!isEmpty(key)) {
            t.setPublishKey(key.toString());
        } else {
            t.setPublishKey(envVariables.getenv(PUBLISH_KEY_ENV));
        }
    }

    private void configureSecret(GradlePortalPublishTask t) {
        Object secret = t.getProject().findProperty("gradle.publish.secret");
        if (!isEmpty(secret)) {
            t.setPublishSecret(secret.toString());
        } else {
            t.setPublishSecret(envVariables.getenv(PUBLISH_SECRET_ENV));
        }
    }
}

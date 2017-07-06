package org.shipkit.internal.gradle.release;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.specs.Spec;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.gradle.UpdateReleaseNotesTask;
import org.shipkit.gradle.release.GradlePortalPublishTask;
import org.shipkit.internal.gradle.ReleaseNotesPlugin;
import org.shipkit.internal.gradle.configuration.BasicValidator;
import org.shipkit.internal.gradle.configuration.LazyConfiguration;
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.util.EnvVariables;

import javax.inject.Inject;

import static org.shipkit.internal.gradle.util.StringUtil.isEmpty;

/**
 * Automated releases to Gradle Plugin portal.
 * Sets up task dependencies and applies configuration needed for automated releases of Gradle plugins.
 *
 * Applies:
 * <ul>
 *     <li>{@link ReleasePlugin}</li>
 *     <li>com.gradle.plugin-publish</li>
 * </ul>
 *
 * Adds tasks:
 * <ul>
 *     <li>'performPublishPlugins' of type {@link GradlePortalPublishTask}</li>
 * </ul>
 *
 * Adds behavior:
 * <ul>
 *     <li>Skips 'performPublishPlugins' task if dryRun is enabled, see {@link ReleaseConfiguration#dryRun}</li>
 * </ul>
 */
public class GradlePortalReleasePlugin implements Plugin<Project> {

    private final static Logger LOG = Logging.getLogger(GradlePortalReleasePlugin.class);

    final static String PUBLISH_KEY_ENV = "GRADLE_PUBLISH_KEY";
    final static String PUBLISH_SECRET_ENV = "GRADLE_PUBLISH_SECRET";
    final static String PERFORM_PUBLISH_TASK = "performPublishPlugins";
    private final static String PUBLISH_KEY_PROPERTY = "gradle.publish.key";
    private final static String PUBLISH_KEY_SECRET = "gradle.publish.secret";

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
                configureKey(t);
                configureSecret(t);
                LazyConfiguration.lazyConfiguration(t, new Runnable() {
                    @Override
                    public void run() {
                        validateSetting(t.getPublishKey(), "publishKey", PUBLISH_KEY_ENV, PUBLISH_KEY_PROPERTY, t);
                        validateSetting(t.getPublishSecret(), "publishSecret", PUBLISH_SECRET_ENV, PUBLISH_KEY_SECRET, t);
                    }
                });

                Task performRelease = project.getTasks().getByName(ReleasePlugin.PERFORM_RELEASE_TASK);
                Task gitPush = project.getTasks().getByName(GitPlugin.GIT_PUSH_TASK);

                performRelease.dependsOn(t); //perform release will actually publish the plugins
                t.mustRunAfter(gitPush); //git push is easier to revers than perform release
                gitPush.mustRunAfter("buildArchives"); //so that we first build plugins to be published, then do git push
                t.dependsOn("buildArchives");

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

        UpdateReleaseNotesTask updateNotes = (UpdateReleaseNotesTask) project.getTasks().getByName(ReleaseNotesPlugin.UPDATE_NOTES_TASK);
        updateNotes.setPublicationRepository("https://plugins.gradle.org/plugin/org.shipkit.java");
    }

    private static void validateSetting(String value, String settingName, String publishKeyEnv, String projectProperty, GradlePortalPublishTask task) {
        BasicValidator.notNull(value, publishKeyEnv, "Gradle Plugin Portal '" + settingName + "' is required. Resolution options:\n" +
                " - export '" + publishKeyEnv + "' env var (recommended for CI, don't commit secrets to VCS!)\n" +
                " - use '" + projectProperty + "' project property\n" +
                " - configure '" + task.getName() + "' task in build file");
    }

    private void configureKey(GradlePortalPublishTask t) {
        Object key = t.getProject().findProperty(PUBLISH_KEY_PROPERTY);
        if (!isEmpty(key)) {
            t.setPublishKey(key.toString());
        } else {
            t.setPublishKey(envVariables.getNonEmptyEnv(PUBLISH_KEY_ENV));
        }
    }

    private void configureSecret(GradlePortalPublishTask t) {
        Object secret = t.getProject().findProperty(PUBLISH_KEY_SECRET);
        if (!isEmpty(secret)) {
            t.setPublishSecret(secret.toString());
        } else {
            t.setPublishSecret(envVariables.getNonEmptyEnv(PUBLISH_SECRET_ENV));
        }
    }
}

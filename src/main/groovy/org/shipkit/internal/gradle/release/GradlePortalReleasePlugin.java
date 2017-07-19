package org.shipkit.internal.gradle.release;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.specs.Spec;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.gradle.notes.UpdateReleaseNotesTask;
import org.shipkit.internal.gradle.ReleaseNotesPlugin;
import org.shipkit.internal.gradle.configuration.BasicValidator;
import org.shipkit.internal.gradle.configuration.LazyConfiguration;
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitPlugin;
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
 * <ul>
 *     <li>Sets 'gradle.publish.key', 'gradle.publish.secret' project properties based on env variables:
 *          GRADLE_PUBLISH_KEY, GRADLE_PUBLISH_SECRET</li>
 *     <li>Validates presence of publish key and secret if 'publishPlugins' task is in the task graph</li>
 *     <li>Skips 'publishPlugins' task if dryRun is enabled, see {@link ReleaseConfiguration#dryRun}</li>
 * </ul>
 */
public class GradlePortalReleasePlugin implements Plugin<Project> {

    private final static Logger LOG = Logging.getLogger(GradlePortalReleasePlugin.class);

    final static String PUBLISH_KEY_ENV = "GRADLE_PUBLISH_KEY";
    final static String PUBLISH_SECRET_ENV = "GRADLE_PUBLISH_SECRET";
    final static String PUBLISH_KEY_PROPERTY = "gradle.publish.key";
    final static String PUBLISH_SECRET_PROPERTY = "gradle.publish.secret";

    private final EnvVariables envVariables;
    final static String PUBLISH_PLUGINS_TASK = "publishPlugins";

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

        final Task publishPlugins = project.getTasks().getByName(PUBLISH_PLUGINS_TASK);

        LazyConfiguration.lazyConfiguration(publishPlugins, new Runnable() {
            @Override
            public void run() {
                authenticate(PUBLISH_KEY_PROPERTY, project, PUBLISH_KEY_ENV, envVariables);
                authenticate(PUBLISH_SECRET_PROPERTY, project, PUBLISH_SECRET_ENV, envVariables);
            }
        });

        Task performRelease = project.getTasks().getByName(ReleasePlugin.PERFORM_RELEASE_TASK);
        Task gitPush = project.getTasks().getByName(GitPlugin.GIT_PUSH_TASK);

        performRelease.dependsOn(publishPlugins); //perform release will actually publish the plugins
        publishPlugins.mustRunAfter(gitPush);     //git push is easier to revers than perform release

        //so that we first build plugins to be published, then do git push, we're using 'buildArchives' for that
        publishPlugins.dependsOn("buildArchives");
        gitPush.mustRunAfter("buildArchives");

        publishPlugins.onlyIf(new Spec<Task>() {
            @Override
            public boolean isSatisfiedBy(Task t) {
                if (conf.isDryRun()) {
                    LOG.info("dryRun is enabled, skipping '{}' using 'onlyIf'", t.getName());
                }
                return !conf.isDryRun();
            }
        });

        UpdateReleaseNotesTask updateNotes = (UpdateReleaseNotesTask) project.getTasks().getByName(ReleaseNotesPlugin.UPDATE_NOTES_TASK);
        updateNotes.setPublicationRepository("https://plugins.gradle.org/plugin/org.shipkit.java");
    }

    private static void authenticate(String projectProperty, Project project, String envVarName, EnvVariables envVariables) {
        Object value = project.findProperty(projectProperty);
        if (isEmpty(value)) {
            value = envVariables.getNonEmptyEnv(envVarName);
            BasicValidator.notNull(value, "Gradle Plugin Portal '" + projectProperty + "' is required. Options:\n" +
                " - export '" + envVarName + "' env var (recommended for CI, don't commit secrets to VCS!)\n" +
                " - use '" + projectProperty + "' project property");
            project.getExtensions().getExtraProperties().set(projectProperty, value);
        }
    }
}

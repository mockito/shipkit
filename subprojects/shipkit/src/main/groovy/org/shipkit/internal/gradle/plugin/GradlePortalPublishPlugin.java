package org.shipkit.internal.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.specs.Spec;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.configuration.BasicValidator;
import org.shipkit.internal.gradle.configuration.LazyConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.snapshot.LocalMavenSnapshotPlugin;
import org.shipkit.internal.util.EnvVariables;

import javax.inject.Inject;

import static org.shipkit.internal.gradle.util.StringUtil.isEmpty;

/**
 * Helps publishing to Gradle plugin portal.
 * Uses env variables to configure key and secret (requirements for publication).
 * Disables publication task in dry run mode.
 * Intended to be applied in a submodule that hosts Gradle plugin code to be published.
 *
 * Applies:
 * <ul>
 *     <li>{@link ShipkitConfigurationPlugin}</li>
 *     <li>com.gradle.plugin-publish</li>
 * </ul>
 *
 * <ul>
 *     <li>Sets 'gradle.publish.key', 'gradle.publish.secret' project properties based on env variables:
 *          GRADLE_PUBLISH_KEY, GRADLE_PUBLISH_SECRET</li>
 *     <li>Validates presence of publish key and secret if 'publishPlugins' task is in the task graph</li>
 *     <li>Skips 'publishPlugins' task if dryRun is enabled, see {@link ShipkitConfiguration#dryRun}</li>
 * </ul>
 *
 */
public class GradlePortalPublishPlugin implements Plugin<Project> {

    private final static Logger LOG = Logging.getLogger(GradlePortalPublishPlugin.class);

    final static String PUBLISH_KEY_ENV = "GRADLE_PUBLISH_KEY";
    final static String PUBLISH_SECRET_ENV = "GRADLE_PUBLISH_SECRET";
    final static String PUBLISH_KEY_PROPERTY = "gradle.publish.key";
    final static String PUBLISH_SECRET_PROPERTY = "gradle.publish.secret";

    private final EnvVariables envVariables;
    public final static String PUBLISH_PLUGINS_TASK = "publishPlugins";

    GradlePortalPublishPlugin(EnvVariables envVariables) {
        this.envVariables = envVariables;
    }

    @Inject public GradlePortalPublishPlugin() {
        this(new EnvVariables());
    }

    @Override
    public void apply(final Project project) {
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();
        project.getPlugins().apply(LocalMavenSnapshotPlugin.class);

        project.getPlugins().apply("com.gradle.plugin-publish");
        //Above also applies 'java' plugin

        final Task publishPlugins = project.getTasks().getByName(PUBLISH_PLUGINS_TASK);

        LazyConfiguration.lazyConfiguration(publishPlugins, new Runnable() {
            @Override
            public void run() {
                authenticate(PUBLISH_KEY_PROPERTY, project, PUBLISH_KEY_ENV, envVariables);
                authenticate(PUBLISH_SECRET_PROPERTY, project, PUBLISH_SECRET_ENV, envVariables);
            }
        });

        publishPlugins.onlyIf(new Spec<Task>() {
            @Override
            public boolean isSatisfiedBy(Task t) {
                if (conf.isDryRun()) {
                    LOG.info("dryRun is enabled, skipping '{}' using 'onlyIf'", t.getName());
                }
                return !conf.isDryRun();
            }
        });
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

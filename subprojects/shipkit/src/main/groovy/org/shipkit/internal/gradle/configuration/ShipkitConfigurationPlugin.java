package org.shipkit.internal.gradle.configuration;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ObjectConfigurationAction;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.init.InitPlugin;
import org.shipkit.internal.gradle.version.VersioningPlugin;
import org.shipkit.version.VersionInfo;

import java.io.File;

/**
 * Adds Gradle DSL extension to the root project so that Shipkit can be configured.
 * Configuration properties are loaded from gradle/shipkit.gradle file when the plugin is applied.
 * This mechanism assures that all properties are accessible during configuration phase.
 * If such file is not present, it will be created automatically with required properties and example values.
 * Important: it will add to the root project because this is where the configuration belongs to!
 * Adds following behavior:
 * <ul>
 *     <li>Adds and preconfigures 'shipkit' extension of type {@link ShipkitConfiguration}</li>
 *     <li>Configures 'shipkit.dryRun' setting based on 'dryRun' Gradle project property</li>
 * </ul>
 *
 * Applies following plugins and preconfigures tasks provided by those plugins:
 *
 * <ul>
 *     <li>{@link InitPlugin}</li>
 *     <li>{@link VersioningPlugin}</li>
 * </ul>
 */
public class ShipkitConfigurationPlugin implements Plugin<Project> {

    private final static Logger LOG = Logging.getLogger(ShipkitConfigurationPlugin.class);
    public static final String SHIPKIT_FILE_RELATIVE_PATH = "gradle/shipkit.gradle";
    public static final String DRY_RUN_PROPERTY = "dryRun";

    private ShipkitConfiguration conf;

    public static File getShipkitFile(Project project) {
        return new File(project.getRootDir(), "gradle/shipkit.gradle");
    }

    public void apply(final Project project) {
        if (project.getParent() == null) {
            //root project, add the extension
            project.getPlugins().apply(InitPlugin.class);
            project.getPlugins().apply(VersioningPlugin.class);
            VersionInfo info = project.getExtensions().getByType(VersionInfo.class);

            conf = project.getRootProject().getExtensions()
                    .create("shipkit", ShipkitConfiguration.class);

            loadConfigFromFile(project.getRootProject(), getShipkitFile(project), conf);

            if (project.hasProperty(DRY_RUN_PROPERTY)) {
                conf.setDryRun(true);
                //TODO (maybe) we can actually implement it so that we automatically preconfigure everything by command line parameters
                //e.g. shipkit.gitHub.repository is also a property
            }

            conf.setPreviousReleaseVersion(info.getPreviousVersion());

        } else {
            //not root project, get extension from root project
            conf = project.getRootProject().getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();
        }
    }

    private static void loadConfigFromFile(final Project rootProject, File shipkitFile, ShipkitConfiguration conf) {
        if (!shipkitFile.exists()) {
            // sets some defaults so that they can't be used to run any task (except for bootstrap ones)
            // but also configuration doesn't fail when running Shipkit for the first time
            // and configuration files are not created yet
            conf.getGitHub().setRepository("unspecified");
            conf.getGitHub().setReadOnlyAuthToken("unspecified");
            LOG.lifecycle("  Configuration file '{}' does not exist. Please run '{}'." +
                "  Getting Started Guide: https://github.com/mockito/shipkit/wiki/Getting-started-with-Shipkit", shipkitFile.getName(), InitPlugin.INIT_SHIPKIT_TASK);
        } else {
            // apply configuration properties from config file
            rootProject.apply(new Action<ObjectConfigurationAction>() {
                @Override
                public void execute(ObjectConfigurationAction action) {
                    action.from(getShipkitFile(rootProject));
                }
            });
        }
    }

    /**
     * Returns the release configuration instance that is hooked up to the root project
     */
    public ShipkitConfiguration getConfiguration() {
        return conf;
    }
}

package org.shipkit.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ObjectConfigurationAction;

import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.version.VersionInfo;

import java.io.File;

/**
 * Adds extension for configuring the release to the root project.
 * Configuration properties are loaded from gradle/shipkit.gradle file when the plugin is applied.
 * This mechanism assures that all properties are accessible during configuration phase.
 * If such file is not present, it will be created automatically with required properties and example values.
 * Important: it will add to the root project because this is where the configuration belong to!
 * Adds following behavior:
 * <ul>
 *     <li>Adds and preconfigures 'shipkit' extension of type {@link ReleaseConfiguration}</li>
 *     <li>Configures 'shipkit.dryRun' setting based on 'shipkit.dryRun' Gradle project property</li>
 * </ul>
 *
 * Applies following plugins and preconfigures tasks provided by those plugins:
 *
 * <ul>
 *     <li>{@link InitPlugin}</li>
 *     <li>{@link VersioningPlugin}</li>
 * </ul>
 */
public class ReleaseConfigurationPlugin implements Plugin<Project> {

    private ReleaseConfiguration configuration;

    public static final String CONFIG_FILE_RELATIVE_PATH = "gradle/shipkit.gradle";
    static final String INIT_CONFIG_FILE_TASK = "initConfigFile";

    public void apply(final Project project) {
        if (project.getParent() == null) {
            //root project, add the extension
            project.getPlugins().apply(InitPlugin.class);
            project.getPlugins().apply(VersioningPlugin.class);
            VersionInfo info = project.getExtensions().getByType(VersionInfo.class);

            configuration = project.getRootProject().getExtensions()
                    .create("shipkit", ReleaseConfiguration.class);

            final File configFile = project.file(CONFIG_FILE_RELATIVE_PATH);

            loadConfigFromFile(project.getRootProject(), configFile);

            if (project.hasProperty("shipkit.dryRun")) {
                //TODO rename to 'dryRun' and expose constant
                //TODO document that we only check for presence of this property
                configuration.setDryRun(true);
                //TODO (maybe) we can actually implement it so that we automatically preconfigure everything by command line parameters
                //e.g. shipkit.gitHub.repository is also a property
            }

            configuration.setPreviousReleaseVersion(info.getPreviousVersion());

            TaskMaker.task(project, INIT_CONFIG_FILE_TASK, InitConfigFileTask.class, new Action<InitConfigFileTask>() {
                @Override
                public void execute(InitConfigFileTask t) {
                    t.setDescription("Creates Shipkit configuration file unless it already exists");
                    t.setConfigFile(configFile);

                    project.getTasks().getByName(InitPlugin.INIT_SHIPKIT_TASK).dependsOn(t);
                }
            });

        } else {
            //not root project, get extension from root project
            configuration = project.getRootProject().getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        }
    }

    private void loadConfigFromFile(Project rootProject, File configFile) {
        if (!configFile.exists()) {
            // sets some defaults so that they can't be used to run any task (except for bootstrap ones)
            // but also configuration doesn't fail when running Shipkit for the first time
            // and configuration files are not created yet
            configuration.getGitHub().setUrl("https://github.com");
            configuration.getGitHub().setApiUrl("https://api.github.com");
            configuration.getGitHub().setRepository("mockito/shipkit");
            configuration.getGitHub().setReadOnlyAuthToken("e7fe8fcfd6ffedac384c8c4c71b2a48e646ed1ab");
        } else {
            // apply configuration properties from config file
            rootProject.apply(new Action<ObjectConfigurationAction>() {
                @Override
                public void execute(ObjectConfigurationAction objectConfigurationAction) {
                    objectConfigurationAction.from(CONFIG_FILE_RELATIVE_PATH);
                }
            });
        }
    }

    /**
     * Returns the release configuration instance that is hooked up to the root project
     */
    public ReleaseConfiguration getConfiguration() {
        return configuration;
    }
}

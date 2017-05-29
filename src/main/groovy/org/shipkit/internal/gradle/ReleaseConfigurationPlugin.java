package org.shipkit.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ObjectConfigurationAction;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.notes.util.IOUtil;
import org.shipkit.version.VersionInfo;

import java.io.File;

/**
 * Adds extension for configuring the release to the root project.
 * Configuration properties are loaded from gradle/shipkit.gradle file when the plugin is applied.
 * This mechanism assures that all properties are accessible during configuration phase.
 * If such file is not present, it will be created automatically with required properties and example values.
 * Important: it will add to the root project because this is where the configuration belong to!
 * Adds following behavior:
 * <ul>
 *     <li>Adds and preconfigures 'releasing' extension of type {@link ReleaseConfiguration}</li>
 *     <li>Configures 'releasing.dryRun' setting based on 'releasing.dryRun' Gradle project property</li>
 * </ul>
 */
public class ReleaseConfigurationPlugin implements Plugin<Project> {

    private ReleaseConfiguration configuration;

    public static final String CONFIG_FILE_RELATIVE_PATH = "gradle/shipkit.gradle";

    public void apply(Project project) {
        if (project.getParent() == null) {
            //root project, add the extension
            project.getPlugins().apply(VersioningPlugin.class);
            VersionInfo info = project.getExtensions().getByType(VersionInfo.class);

            configuration = project.getRootProject().getExtensions()
                    .create("releasing", ReleaseConfiguration.class);

            loadShipKitConfigFile(project.getRootProject());

            if (project.hasProperty("releasing.dryRun")) {
                Object value = project.getProperties().get("releasing.dryRun");
                configuration.setDryRun(!"false".equals(value));
                //TODO we can actually implement it so that we automatically preconfigure everything by command line parameters
                //e.g. releasing.gitHub.repository is also a property
            }

            configuration.setPreviousReleaseVersion(info.getPreviousVersion());
        } else {
            //not root project, get extension from root project
            configuration = project.getRootProject().getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        }
    }

    private void loadShipKitConfigFile(Project rootProject) {
        final File configFile = rootProject.file(CONFIG_FILE_RELATIVE_PATH);
        if (!configFile.exists()) {
            createShipKitConfigFile(configFile);
            throw new GradleException("Config file created at " + configFile.getAbsolutePath() + ". Please configure it and rerun the task.");
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

    private void createShipKitConfigFile(File configFile) {
        String content =
                new TemplateResolver(DEFAULT_SHIPKIT_CONFIG_FILE_CONTENT)
                    .withProperty("gitHub.repository", "mockito/mockito-release-tools-example")
                    .withProperty("gitHub.writeAuthUser", "shipkit")
                    .withProperty("gitHub.readOnlyAuthToken", "e7fe8fcfd6ffedac384c8c4c71b2a48e646ed1ab")

                    .withProperty("bintray.pkg.repo", "examples")
                    .withProperty("bintray.pkg.user", "szczepiq")
                    .withProperty("bintray.pkg.userOrg", "shipkit")
                    .withProperty("bintray.pkg.name", "basic")
                    .withProperty("bintray.pkg.licenses", "['MIT']")
                    .withProperty("bintray.pkg.labels", "['continuous delivery', 'release automation', 'mockito']")

                    .resolve();

        IOUtil.writeFile(configFile, content);
    }

    /**
     * Returns the release configuration instance that is hooked up to the root project
     */
    public ReleaseConfiguration getConfiguration() {
        return configuration;
    }

    static final String DEFAULT_SHIPKIT_CONFIG_FILE_CONTENT =
            "//This file was created automatically and is intented to be checked-in.\n" +
            "releasing {\n"+
            "   gitHub.repository = \"@gitHub.repository@\"\n"+
            "   gitHub.readOnlyAuthToken = \"@gitHub.readOnlyAuthToken@\"\n"+
            "   gitHub.writeAuthUser = \"@gitHub.writeAuthUser@\"\n"+
            "}\n"+
            "\n"+
            "allprojects {\n"+
            "   plugins.withId(\"org.mockito.mockito-release-tools.bintray\") {\n"+
            "       bintray {\n"+
            "           pkg {\n"+
            "               repo = '@bintray.pkg.repo@'\n"+
            "               user = '@bintray.pkg.user@'\n"+
            "               userOrg = '@bintray.pkg.userOrg@'\n"+
            "               name = '@bintray.pkg.name@'\n"+
            "               licenses = @bintray.pkg.licenses@\n"+
            "               labels = @bintray.pkg.labels@\n"+
            "           }\n"+
            "       }\n"+
            "   }\n"+
            "}\n";
}

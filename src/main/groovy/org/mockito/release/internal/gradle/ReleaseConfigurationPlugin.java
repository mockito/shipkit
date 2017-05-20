package org.mockito.release.internal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.notes.util.IOUtil;
import org.mockito.release.version.VersionInfo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Adds extension for configuring the release to the root project.
 * Important: it will add to the root project because this is where the configuration belong to!
 * Adds following behavior:
 * <ul>
 *     <li>Adds and preconfigures 'releasing' extension of type {@link ReleaseConfiguration}</li>
 *     <li>Configures 'releasing.dryRun' setting based on 'releasing.dryRun' Gradle project property</li>
 *     <li>Configures 'releasing.notableRelease' setting based on the version we are currently building</li>
 * </ul>
 */
public class ReleaseConfigurationPlugin implements Plugin<Project> {

    private ReleaseConfiguration configuration;

    private static final String CONFIG_FILE_RELATIVE_PATH = "gradle/shipkit.gradle";

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

            configuration.setNotableRelease(info.isNotableRelease());
            configuration.setPreviousReleaseVersion(info.getPreviousVersion());
        } else {
            //not root project, get extension from root project
            configuration = project.getRootProject().getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        }
    }

    private void loadShipKitConfigFile(Project rootProject) {
        File configFile = rootProject.file(CONFIG_FILE_RELATIVE_PATH);
        if (!configFile.exists()) {
            createShipKitConfigFile(configFile);
            //throw new GradleException("Config file created at " + configFile.getAbsolutePath() + ". Please configure it and rerun task.");
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("from", configFile);
            rootProject.apply(map);
        }
    }

    private void createShipKitConfigFile(File configFile) {
        String content =
                new ConfigurationFileBuilder("releasing")
                    .withProperty("gitHub.repository", "mockito/mockito")
                    .withProperty("gitHub.writeAuthUser", "wwilk")
                    .withProperty("gitHub.writeAuthToken", new ConfigurationFileBuilder.Expression("System.getenv(\"GH_WRITE_TOKEN\")"))
                    .withProperty("gitHub.readOnlyAuthToken", "e7fe8fcfd6ffedac384c8c4c71b2a48e646ed1ab")

                    .withProperty("git.user", "Mockito Release Tools")
                    .withProperty("git.email", "<mockito.release.tools@gmail.com>")
                    .withProperty("git.releasableBranchRegex", "master|release/.+")

                    .withProperty("releaseNotes.file", "docs/release-notes.md")
                    .withProperty("releaseNotes.notableFile", "docs/notable-release-notes.md")
                    .withProperty("releaseNotes.labelMapping", labelMapping())

                    .withProperty("team.developers", asList("szczepiq:Szczepan Faber"))
                    .withProperty("team.contributors", asList("mstachniuk:Marcin Stachniuk", "wwilk:Wojtek Wilk"))
                    .build();

        IOUtil.writeFile(configFile, content);
    }

    private Map<String, String> labelMapping(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("noteworthy","Noteworthy");
        result.put("bugfix","Bugfixes");
        return result;
    }

    /**
     * Returns the release configuration instance that is hooked up to the root project
     */
    public ReleaseConfiguration getConfiguration() {
        return configuration;
    }
}

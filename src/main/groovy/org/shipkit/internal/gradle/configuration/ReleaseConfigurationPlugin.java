package org.shipkit.internal.gradle.configuration;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ObjectConfigurationAction;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.internal.gradle.VersioningPlugin;
import org.shipkit.gradle.init.InitShipkitFileTask;
import org.shipkit.internal.gradle.git.GitRemoteOriginPlugin;
import org.shipkit.internal.gradle.init.InitPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.util.ResultHandler;
import org.shipkit.internal.version.VersionInfo;

import java.io.File;

/**
 * Adds Gradle DSL extension to the root project so that Shipkit can be configured.
 * Configuration properties are loaded from gradle/shipkit.gradle file when the plugin is applied.
 * This mechanism assures that all properties are accessible during configuration phase.
 * If such file is not present, it will be created automatically with required properties and example values.
 * Important: it will add to the root project because this is where the configuration belong to!
 * Adds following behavior:
 * <ul>
 *     <li>Adds and preconfigures 'shipkit' extension of type {@link ReleaseConfiguration}</li>
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
public class ReleaseConfigurationPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(ReleaseConfigurationPlugin.class);

    public static final String SHIPKIT_FILE_RELATIVE_PATH = "gradle/shipkit.gradle";
    static final String INIT_SHIPKIT_FILE_TASK = "initShipkitFile";
    public static final String DRY_RUN_PROPERTY = "dryRun";
    private static final String FALLBACK_GITHUB_REPO = "mockito/shipkit-example";

    private ReleaseConfiguration configuration;

    public void apply(final Project project) {
        if (project.getParent() == null) {
            //root project, add the extension
            project.getPlugins().apply(InitPlugin.class);
            project.getPlugins().apply(VersioningPlugin.class);
            final GitRemoteOriginPlugin gitRemoteOriginPlugin = project.getPlugins().apply(GitRemoteOriginPlugin.class);
            VersionInfo info = project.getExtensions().getByType(VersionInfo.class);

            configuration = project.getRootProject().getExtensions()
                    .create("shipkit", ReleaseConfiguration.class);

            final File shipkitFile = project.file(SHIPKIT_FILE_RELATIVE_PATH);

            loadConfigFromFile(project.getRootProject(), shipkitFile);

            if (project.hasProperty(DRY_RUN_PROPERTY)) {
                configuration.setDryRun(true);
                //TODO (maybe) we can actually implement it so that we automatically preconfigure everything by command line parameters
                //e.g. shipkit.gitHub.repository is also a property
            }

            configuration.setPreviousReleaseVersion(info.getPreviousVersion());

            TaskMaker.task(project, INIT_SHIPKIT_FILE_TASK, InitShipkitFileTask.class, new Action<InitShipkitFileTask>() {
                @Override
                public void execute(final InitShipkitFileTask t) {
                    t.setDescription("Creates Shipkit configuration file unless it already exists");
                    t.setShipkitFile(shipkitFile);

                    project.getTasks().getByName(InitPlugin.INIT_SHIPKIT_TASK).dependsOn(t);

                    gitRemoteOriginPlugin.provideOriginTo(t, new ResultHandler<GitRemoteOriginPlugin.GitOriginAuth>() {
                        @Override
                        public void onSuccess(GitRemoteOriginPlugin.GitOriginAuth result) {
                            t.setOriginRepoName(result.getOriginRepositoryName());
                        }

                        @Override
                        public void onFailure(RuntimeException e) {
                            LOG.lifecycle("  Problems getting url of git remote origin (run with --debug to find out more).\n" +
                                "  Using fallback '" + FALLBACK_GITHUB_REPO + "' instead.\n" +
                                "  Please update GitHub repository in '" + shipkitFile + "' file.\n");
                            LOG.debug("  Problems getting url of git remote origin", e);
                            t.setOriginRepoName(FALLBACK_GITHUB_REPO);
                        }
                    });
                }
            });

        } else {
            //not root project, get extension from root project
            configuration = project.getRootProject().getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        }
    }

    private void loadConfigFromFile(Project rootProject, File shipkitFile) {
        if (!shipkitFile.exists()) {
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
                    objectConfigurationAction.from(SHIPKIT_FILE_RELATIVE_PATH);
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

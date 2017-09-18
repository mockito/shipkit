package org.shipkit.internal.gradle.init;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.init.InitShipkitFileTask;
import org.shipkit.gradle.init.InitTravisTask;
import org.shipkit.gradle.init.InitVersioningTask;
import org.shipkit.gradle.version.BumpVersionFileTask;
import org.shipkit.internal.gradle.configuration.DeferredConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitAuthPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.gradle.version.VersioningPlugin;

import java.io.File;

/**
 * Creates task initShipkit that all other init tasks should depend on
 * so that running it would create all configuration needed to start Shipkit.
 *
 * Applies plugins:
 * <ul>
 *     <li>{@link VersioningPlugin}
 *      - so that 'initVersioning' task knows what version file needs to be generated.</li>
 * </ul>
 *
 * Adds tasks:
 * <ul>
 *     <li>'initTravis' - of type {@link InitTravisTask} - generates '.travis.yml' file (check it in!).</li>
 *     <li>'initVersioning' - of type {@link InitVersioningTask} - generates 'version.properties' file (check it in!).</li>
 *     <li>'initShipkitFile' - of type {@link InitShipkitFileTask} - generates 'gradle/shipkit.gradle' file (check it in!).</li>
 *     <li>'initShipkit' - of type {@link org.gradle.api.DefaultTask} - depends on other 'init' tasks.
 *          Run it to initialize Shipkit. Generated files are intended to be checked in to VCS (Git).</li>
 * </ul>
 */
public class InitPlugin implements Plugin<Project> {

    private final static Logger LOG = Logging.getLogger(InitPlugin.class);

    public static final String INIT_SHIPKIT_FILE_TASK = "initShipkitFile";
    public static final String INIT_VERSIONING_TASK = "initVersioning";
    public static final String INIT_SHIPKIT_TASK = "initShipkit";
    public static final String INIT_TRAVIS_TASK = "initTravis";

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(VersioningPlugin.class);
        project.getPlugins().apply(ShipkitConfigurationPlugin.class);
        final GitAuthPlugin gitAuthPlugin = project.getRootProject().getPlugins().apply(GitAuthPlugin.class);

        TaskMaker.task(project, INIT_TRAVIS_TASK, InitTravisTask.class, new Action<InitTravisTask>() {
            public void execute(InitTravisTask t) {
                t.setDescription("Creates '.travis.yml' file if not already present.");
                t.setOutputFile(new File(project.getRootDir(), ".travis.yml"));
            }
        });

        final BumpVersionFileTask bump = (BumpVersionFileTask) project.getTasks().getByName(VersioningPlugin.BUMP_VERSION_FILE_TASK);
        TaskMaker.task(project, INIT_VERSIONING_TASK, InitVersioningTask.class, new Action<InitVersioningTask>() {
            @Override
            public void execute(InitVersioningTask t) {
                t.setDescription("Creates version.properties file if it doesn't exist");
                t.setVersionFile(bump.getVersionFile());
            }
        });

        TaskMaker.task(project, INIT_SHIPKIT_FILE_TASK, InitShipkitFileTask.class, new Action<InitShipkitFileTask>() {
            @Override
            public void execute(final InitShipkitFileTask t) {
                final File shipkitFile = ShipkitConfigurationPlugin.getShipkitFile(project);
                t.setDescription("Creates Shipkit configuration file unless it already exists");
                t.setShipkitFile(shipkitFile);

                gitAuthPlugin.provideAuthTo(t, new Action<GitAuthPlugin.GitAuth>() {
                    public void execute(GitAuthPlugin.GitAuth gitAuth) {
                        t.setOriginRepoName(gitAuth.getRepositoryName());
                    }
                });
            }
        });

        TaskMaker.task(project, INIT_SHIPKIT_TASK, new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Initializes Shipkit");
                t.dependsOn(INIT_TRAVIS_TASK, INIT_VERSIONING_TASK, INIT_SHIPKIT_FILE_TASK);
            }
        });

        //The user might configure the 'group' later, after the plugin is applied
        DeferredConfiguration.deferredConfiguration(project, new Runnable() {
            @Override
            public void run() {
                if (project.getGroup() == null) {
                    LOG.info("  Gradle project does not have 'group' property configured yet." +
                            "\n  Shipkit is setting the group to 'org.shipkit.bootstrap' so that it has a reasonable default." +
                            "\n  It is recommended that you configure the 'group' to your choice.");
                    project.setGroup("org.shipkit.bootstrap");
                }
            }
        });
    }
}

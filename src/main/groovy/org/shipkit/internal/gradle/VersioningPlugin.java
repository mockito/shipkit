package org.shipkit.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.BumpVersionFileTask;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.init.InitPlugin;
import org.shipkit.gradle.init.InitVersioningTask;
import org.shipkit.internal.gradle.util.StringUtil;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.version.Version;
import org.shipkit.internal.version.VersionInfo;

import java.io.File;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

/**
 * The plugin adds following tasks:
 *
 * <ul>
 *     <li>bumpVersionFile - increments version in "version.properties" file,
 *     see {@link BumpVersionFileTask}</li>
 * </ul>
 *
 * The plugin adds following extensions:
 *
 * <ul>
 *     <li>project.extensions.'VersionInfo' property
 *     of type {@link VersionInfo} that contains version information</li>
 * </ul>
 *
 * Also, the plugin configures all projects' version property to the value specified in "version.properties"
 *
 * BEWARE! If version.properties doesn't exists, this plugin will create it automatically and set
 * version value to project.version
 *
 * Plugin adds bumped version changes if {@link GitPlugin} applied
 */
public class VersioningPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(VersioningPlugin.class);

    public static final String VERSION_FILE_NAME = "version.properties";

    public static final String BUMP_VERSION_FILE_TASK = "bumpVersionFile";
    static final String INIT_VERSIONING_TASK = "initVersioning";

    public void apply(final Project project) {
        project.getPlugins().apply(InitPlugin.class);

        final File versionFile = project.file(VERSION_FILE_NAME);

        final VersionInfo versionInfo = versionFile.exists() ?
                Version.versionInfo(versionFile) :
                Version.defaultVersionInfo(versionFile, project.getVersion().toString());

        project.getExtensions().add(VersionInfo.class.getName(), versionInfo);

        final String version = versionInfo.getVersion();
        LOG.lifecycle("  Building version '{}' (value loaded from '{}' file).", version, versionFile.getName());

        project.allprojects(new Action<Project>() {
            @Override
            public void execute(Project project) {
                project.setVersion(version);
            }
        });

        TaskMaker.task(project, BUMP_VERSION_FILE_TASK, BumpVersionFileTask.class, new Action<BumpVersionFileTask>() {
            public void execute(final BumpVersionFileTask t) {
                t.setDescription("Increments version number in " + versionFile.getName());
                t.setVersionFile(versionFile);
                String versionChangeMessage = formatVersionInformationInCommitMessage(version, versionInfo.getPreviousVersion());
                GitPlugin.registerChangesForCommitIfApplied(singletonList(versionFile), versionChangeMessage, t);
            }
        });

        TaskMaker.task(project, INIT_VERSIONING_TASK, InitVersioningTask.class, new Action<InitVersioningTask>() {
            @Override
            public void execute(InitVersioningTask t) {
                t.setDescription("Creates version.properties file if it doesn't exist");
                t.setVersionFile(versionFile);
                project.getTasks().getByName(InitPlugin.INIT_SHIPKIT_TASK).dependsOn(t);
            }
        });
    }

    private String formatVersionInformationInCommitMessage(String version, String previousVersion) {
        String versionMessage = format("%s release", version);
        if (StringUtil.isEmpty(previousVersion)) {
            return versionMessage;
        } else {
            return format("%s (previous %s)", versionMessage, previousVersion);
        }
    }

}

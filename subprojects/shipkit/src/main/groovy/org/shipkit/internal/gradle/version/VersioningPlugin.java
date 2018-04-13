package org.shipkit.internal.gradle.version;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.version.BumpVersionFileTask;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.snapshot.LocalSnapshotPlugin;
import org.shipkit.internal.gradle.util.StringUtil;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.version.VersionInfo;

import java.io.File;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

/**
 * The plugin adds following tasks:
 *
 * <ul>
 *     <li>Adds task 'bumpVersionFile' - increments version in "version.properties" file,
 *     see {@link BumpVersionFileTask}</li>
 *     <li>Applies {@link LocalSnapshotPlugin} for 'snapshot' task.
 *     Will add "-SNAPSHOT" postfix to version in case 'snapshot' task is requested</li>
 * </ul>
 *
 * The plugin adds following extensions:
 *
 * <ul>
 *     <li>project.extensions.'VersionInfo' property
 *     of type {@link VersionInfo} that contains version information</li>
 * </ul>
 *
 * Added behavior:
 *
 * <ul>
 *     <li>plugin loads 'version.properties' file to identify the version to build</li>
 *     <li>if 'version.properties' does not exist, the plugin uses project version as declared in build.gradle file</li>
 *     <li>'bumpVersionFile' task participates in 'gitCommit' task if {@link GitPlugin} is also applied</li>
 * </ul>
 *
 * Also, the plugin configures all projects' version property to the value specified in "version.properties"
 */
public class VersioningPlugin implements Plugin<Project> {

    public static final String VERSION_FILE_NAME = "version.properties";

    public static final String BUMP_VERSION_FILE_TASK = "bumpVersionFile";

    public void apply(final Project project) {
        LocalSnapshotPlugin snapshotPlugin = project.getPlugins().apply(LocalSnapshotPlugin.class);

        final File versionFile = project.file(VERSION_FILE_NAME);

        final VersionInfo versionInfo = new VersionInfoFactory().createVersionInfo(versionFile,
            project.getVersion(), snapshotPlugin.isSnapshot());

        project.getExtensions().add(VersionInfo.class.getName(), versionInfo);
        final String version = versionInfo.getVersion();

        project.allprojects(project1 -> project1.setVersion(version));

        TaskMaker.task(project, BUMP_VERSION_FILE_TASK, BumpVersionFileTask.class, t -> {
            t.setDescription("Increments version number in " + versionFile.getName());
            t.setVersionFile(versionFile);
            String versionChangeMessage = formatVersionInformationInCommitMessage(version, versionInfo.getPreviousVersion());
            GitPlugin.registerChangesForCommitIfApplied(singletonList(versionFile), versionChangeMessage, t);
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

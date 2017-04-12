package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.mockito.release.gradle.BumpVersionFileTask;
import org.mockito.release.internal.gradle.util.TaskMaker;
import org.mockito.release.version.Version;
import org.mockito.release.version.VersionInfo;

import java.io.File;

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
 *     <li>'project.ext.release_notable' boolean property
 *     that contains information if the current version is a notable release</li>
 *     <li>project.extensions.'org.mockito.release.version.VersionInfo' property
 *     of type {@link VersionInfo} that contains version information</li>
 * </ul>
 *
 * Also, the plugin configures all projects' version property to the value specified in "version.properties"
 */
public class VersioningPlugin implements Plugin<Project> {

    private static Logger LOG = Logging.getLogger(VersioningPlugin.class);

    public void apply(Project project) {
        //TODO "version.properties" is hardcoded all over the place.
        // At the very least we should have a constant in this plugin.
        final File versionFile = new File(project.getRootDir(), "version.properties");
        VersionInfo versionInfo = Version.versionInfo(versionFile);

        //TODO let's add unit tests
        project.getExtensions().add(VersionInfo.class.getName(), versionInfo);
        project.getExtensions().getExtraProperties().set("release_notable", versionInfo.isNotableRelease());

        final String version = versionInfo.getVersion();
        LOG.lifecycle("  Building version '{}' (value loaded from '{}' file).", version, versionFile.getName());

        project.allprojects(new Action<Project>() {
            @Override
            public void execute(Project project) {
                project.setVersion(version);
            }
        });

        TaskMaker.task(project, "bumpVersionFile", BumpVersionFileTask.class, new Action<BumpVersionFileTask>() {
            public void execute(BumpVersionFileTask t) {
                t.setVersionFile(versionFile);
                t.setDescription("Increments version number in " + versionFile.getName());
            }
        });
    }
}
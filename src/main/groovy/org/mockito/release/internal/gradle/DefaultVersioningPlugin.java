package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.mockito.release.gradle.VersioningPlugin;
import org.mockito.release.version.Version;
import org.mockito.release.version.VersionFile;

import java.io.File;

import static org.mockito.release.internal.gradle.util.CommonSettings.TASK_GROUP;

public class DefaultVersioningPlugin implements VersioningPlugin {

    private static Logger LOG = Logging.getLogger(DefaultVersioningPlugin.class);

    public void apply(Project project) {
        final File versionFile = new File(project.getRootDir(), "version.properties");
        VersionFile versionInfo = Version.versionFile(versionFile);
        project.getExtensions().add(VersionFile.class.getName(), versionInfo);

        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
        final String version;
        if (ext.has("release_version")) {
            version = ext.get("release_version").toString();
            LOG.lifecycle("  Using version '{}' supplied via 'release_version' project property.", version);
        } else {
            version = versionInfo.getVersion();
            LOG.lifecycle("  Using version '{}' from '{}' file.", version, versionFile.getName());
        }

        project.allprojects(new Action<Project>() {
            @Override
            public void execute(Project project) {
                project.setVersion(version);
            }
        });

        project.getTasks().create("bumpVersionFile", DefaultBumpVersionFileTask.class, new Action<DefaultBumpVersionFileTask>() {
            public void execute(DefaultBumpVersionFileTask t) {
                t.setVersionFile(versionFile);
                t.setDescription("Increments version number in " + versionFile.getName());
                t.setGroup(TASK_GROUP);
            }
        });
    }

}
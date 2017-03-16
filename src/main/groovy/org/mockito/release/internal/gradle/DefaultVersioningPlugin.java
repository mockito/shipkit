package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.mockito.release.gradle.BumpVersionFileTask;
import org.mockito.release.gradle.VersioningPlugin;
import org.mockito.release.version.Version;

import java.io.File;

import static org.mockito.release.internal.gradle.CommonSettings.TASK_GROUP;

public class DefaultVersioningPlugin implements VersioningPlugin {

    public void apply(Project project) {
        File versionFile = project.file("version.properties");
        final String version = Version.versionFile(versionFile).getVersion();

        project.allprojects(new Action<Project>() {
            @Override
            public void execute(Project project) {
                project.setVersion(version);
            }
        });

        BumpVersionFileTask task = project.getTasks().create("bumpVersionFile", DefaultBumpVersionFileTask.class);
        task.setVersionFile(versionFile);
        task.setDescription("Increments version number in the properties file that contains the version.");
        task.setGroup(TASK_GROUP);
    }
}
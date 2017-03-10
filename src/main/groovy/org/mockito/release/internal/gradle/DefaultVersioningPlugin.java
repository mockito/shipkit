package org.mockito.release.internal.gradle;

import org.gradle.api.Project;
import org.mockito.release.gradle.notes.BumpVersionFileTask;
import org.mockito.release.gradle.notes.VersioningPlugin;
import org.mockito.release.version.Version;

import java.io.File;

import static org.mockito.release.internal.gradle.CommonSettings.TASK_GROUP;

public class DefaultVersioningPlugin implements VersioningPlugin {

    public void apply(Project project) {
        File versionFile = project.file("version.properties");

        project.setVersion(Version.versionFile(versionFile).getVersion());

        BumpVersionFileTask task = project.getTasks().create("bumpVersionFile", DefaultBumpVersionFileTask.class);
        task.setVersionFile(versionFile);
        task.setDescription("Increments version number in the properties file that contains the version.");
        task.setGroup(TASK_GROUP);
    }
}
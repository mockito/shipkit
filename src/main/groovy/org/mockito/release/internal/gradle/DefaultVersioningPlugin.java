package org.mockito.release.internal.gradle;

import org.gradle.api.Project;
import org.mockito.release.gradle.notes.BumpVersionFileTask;
import org.mockito.release.gradle.notes.VersioningPlugin;

import static org.mockito.release.internal.gradle.CommonSettings.TASK_GROUP;

public class DefaultVersioningPlugin implements VersioningPlugin {

    public void apply(Project project) {
        BumpVersionFileTask task = project.getTasks().create("bumpVersionFile", DefaultBumpVersionFileTask.class);
        task.setVersionFile(project.file("version.properties"));
        task.setDescription("Increments version number in the properties file that contains the version.");
        task.setGroup(TASK_GROUP);
    }
}

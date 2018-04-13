package org.shipkit.internal.gradle.snapshot;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class LocalMavenSnapshotPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(LocalSnapshotPlugin.class);
        project.getPlugins().apply("maven");

        Task snapshotTask = project.getTasks().getByName(LocalSnapshotPlugin.SNAPSHOT_TASK);
        snapshotTask.dependsOn("install");
    }
}

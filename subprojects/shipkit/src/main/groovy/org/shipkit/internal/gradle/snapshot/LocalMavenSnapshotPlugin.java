package org.shipkit.internal.gradle.snapshot;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

/**
 * <ul>
 *     <li>Applies {@link LocalSnapshotPlugin} for 'snapshot' task</li>
 *     <li>Applies Gradle's built-in 'maven' plugin for 'install' task</li>
 *     <li>Makes 'snapshot' task depend on 'install' so that
 *          you can run 'snapshot' to create locally installed snapshot</li>
 * </ul>
 */
public class LocalMavenSnapshotPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(LocalSnapshotPlugin.class);
        project.getPlugins().apply("maven");

        Task snapshotTask = project.getTasks().getByName(LocalSnapshotPlugin.SNAPSHOT_TASK);
        snapshotTask.dependsOn("install");
    }
}

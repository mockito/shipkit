package org.shipkit.internal.gradle.snapshot;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.util.TaskMaker;

public class LocalSnapshotPlugin implements Plugin<Project> {

    public static final String SNAPSHOT_TASK = "snapshot";

    @Override
    public void apply(Project project) {
        TaskMaker.task(project, SNAPSHOT_TASK, t -> {
            t.setDescription("Depends on specific tasks that create local snapshot files.");
        });
    }
}

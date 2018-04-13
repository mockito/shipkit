package org.shipkit.internal.gradle.snapshot;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.gradle.util.TaskMaker;

import static org.shipkit.internal.gradle.util.Specs.withName;

public class LocalSnapshotPlugin implements Plugin<Project> {

    public static final String SNAPSHOT_TASK = "snapshot";
    private final static Logger LOG = Logging.getLogger(LocalSnapshotPlugin.class);
    private SnapshotInfo info;

    @Override
    public void apply(Project project) {
        boolean isSnapshot = project.getGradle().getStartParameter().getTaskNames().contains(SNAPSHOT_TASK);
        info = new SnapshotInfo(isSnapshot);

        TaskMaker.task(project, SNAPSHOT_TASK, t -> {
            t.setDescription("Depends on specific tasks that create local snapshot files.");

            if (isSnapshot) {
                project.getTasks().matching(withName("javadoc", "groovydoc")).all(doc -> {
                    LOG.info("{} - disabled to speed up the 'snapshot' build", t.getPath());
                    doc.setEnabled(false);
                });
            }
        });
    }

    public SnapshotInfo getSnapshotInfo() {
        return info;
    }

    public static class SnapshotInfo {

        private final boolean isSnapshot;

        SnapshotInfo(boolean isSnapshot) {
            this.isSnapshot = isSnapshot;
        }

        public boolean isSnapshot() {
            return isSnapshot;
        }
    }
}

package org.shipkit.internal.gradle.snapshot;

import org.gradle.StartParameter;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.gradle.util.TaskMaker;

import static org.shipkit.internal.gradle.util.Specs.withName;

/**
 * <ul>
 *     <li>Adds 'snapshot' task with no behavior.
 *          The task will depend on specific tasks that create and install local snapshot</li>
 *     <li>Checks if 'snapshot' task is requested from command line
 *          by inspecting {@link StartParameter#getTaskNames()}.
 *          If requested then tasks with name 'javadoc' and 'groovydoc' are automatically disabled.
 *          This way, we make building snapshots fast - we don't need docs for local testing of snapshot.
 *          </li>
 * </ul>
 */
public class LocalSnapshotPlugin implements Plugin<Project> {

    public static final String SNAPSHOT_TASK = "snapshot";
    private final static Logger LOG = Logging.getLogger(LocalSnapshotPlugin.class);
    private boolean isSnapshot;

    @Override
    public void apply(Project project) {
        this.isSnapshot = project.getGradle().getStartParameter().getTaskNames().contains(SNAPSHOT_TASK);

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

    public boolean isSnapshot() {
        return isSnapshot;
    }
}

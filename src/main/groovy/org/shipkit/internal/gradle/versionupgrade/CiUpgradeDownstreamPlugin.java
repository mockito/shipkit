package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.exec.ShipkitExecTask;
import org.shipkit.internal.gradle.release.CiReleasePlugin;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.exec.ExecCommandFactory.execCommand;

/**
 * The plugin applies following plugins:
 *
 * <ul>
 *     <li>{@link CiReleasePlugin}</li>
 *     <li>{@link UpgradeDownstreamPlugin}</li>
 * </ul>
 *
 * It adds "upgradeDownstream" task to the execution commands of "ciPerformRelease" task.
 */
public class CiUpgradeDownstreamPlugin implements Plugin<Project>{

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(CiReleasePlugin.class);
        project.getPlugins().apply(UpgradeDownstreamPlugin.class);

        ShipkitExecTask ciPerformReleaseTask = (ShipkitExecTask) project.getTasks().findByName(CiReleasePlugin.CI_PERFORM_RELEASE_TASK);

        ciPerformReleaseTask.getExecCommands().add(execCommand(
                "Upgrading downstream projects", asList("./gradlew", UpgradeDownstreamPlugin.UPGRADE_DOWNSTREAM_TASK)));
    }
}

package org.shipkit.internal.gradle.release;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.java.ComparePublicationsTask;
import org.shipkit.gradle.release.ReleaseNeededTask;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitBranchPlugin;
import org.shipkit.internal.gradle.java.ComparePublicationsPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.util.DeprecatedWarning;

/**
 * Adds tasks for checking if release is needed.
 * <br>
 * If the build is executed on a releasable branch the following criteria are checked:
 *  <ul>
 *      <li>SKIP_RELEASE env variable: no release if this environment variable is available</li>
 *      <li>[ci skip-release] commit message: no release if this commit message is available</li>
 *      <li>PR build: no release if the build job is a pull request build</li>
 *      <li>skipComparePublications task property (ReleaseNeededTask): if the property is true, then comparing publications is skipped and the release can be triggered even if publications are identical. See {@link ReleaseNeededTask#isSkipComparePublications()} for a proper use case.</li>
 *      <li>[ci skip-compare-publications] commit message: if this commit message is available, then comparing publications is skipped and the release can be triggered even if publications are identical.</li>
 *      <li>compare publications: release if previous publication is not identical to the current publication</li>
 *  </ul>
 *
 * Applies following plugins and preconfigures tasks provided by those plugins:
 *
 * <ul>
 *     <li>{@link ShipkitConfigurationPlugin}</li>
 *     <li>{@link GitBranchPlugin}</li>
 * </ul>
 *
 * Adds following tasks:
 *
 * <ul>
 *     <li>assertReleaseNeeded - {@link ReleaseNeededTask}
 *      - DEPRECATED - checks if release is needed and fails the build if not needed. Not used currently.</li>
 *     <li>releaseNeeded - {@link ReleaseNeededTask}
 *     - prints information if the release is needed. Useful for testing.</li>
 * </ul>
 */
public class ReleaseNeededPlugin implements Plugin<Project> {

    public final static String ASSERT_RELEASE_NEEDED_TASK = "assertReleaseNeeded";
    public final static String RELEASE_NEEDED = "releaseNeeded";

    @Override
    public void apply(Project project) {
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        //Task that throws an exception when release is not needed
        //We used it originally to prevent Travis CI from releasing when release was not needed.
        //Kept for backwards compatibility
        ReleaseNeededTask assertReleaseNeededTask = releaseNeededTask(project, ASSERT_RELEASE_NEEDED_TASK, conf);
        assertReleaseNeededTask.setExplosive(true)
            .setDescription("[DEPRECATED] Asserts that criteria for the release are met and throws exception if release is not needed.");
        assertReleaseNeededTask.doFirst(task -> DeprecatedWarning.warn(task.getName(), "Please use '" + RELEASE_NEEDED + "' task instead."));

        //Below task is useful for testing. It will not throw an exception but will run the code that check is release is needed
        //and it will print the information to the console.
        releaseNeededTask(project, RELEASE_NEEDED, conf)
            .setExplosive(false)
            .setDescription("Checks and prints to the console if criteria for the release are met.");
    }

    private static ReleaseNeededTask releaseNeededTask(final Project project, String taskName,
                                                       final ShipkitConfiguration conf) {
        return TaskMaker.task(project, taskName, ReleaseNeededTask.class, new Action<ReleaseNeededTask>() {
            public void execute(final ReleaseNeededTask t) {
                t.setDescription("Asserts that criteria for the release are met and throws exception if release not needed.");
                t.setExplosive(true);

                project.allprojects(new Action<Project>() {
                    public void execute(final Project subproject) {
                        subproject.getPlugins().withType(ComparePublicationsPlugin.class, new Action<ComparePublicationsPlugin>() {
                            public void execute(ComparePublicationsPlugin p) {
                                // make this task depend on all comparePublications tasks
                                ComparePublicationsTask task = (ComparePublicationsTask) subproject.getTasks().getByName(ComparePublicationsPlugin.COMPARE_PUBLICATIONS_TASK);
                                t.dependsOn(task);
                                t.getComparisonResults().add(task.getComparisonResult());
                            }
                        });
                    }
                });

                t.setReleasableBranchRegex(conf.getGit().getReleasableBranchRegex());

                project.getPlugins().apply(GitBranchPlugin.class)
                        .provideBranchTo(t, new Action<String>() {
                            public void execute(String branch) {
                                t.setBranch(branch);
                            }
                        });
            }
        });
    }
}

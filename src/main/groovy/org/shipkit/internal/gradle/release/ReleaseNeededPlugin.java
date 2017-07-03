package org.shipkit.internal.gradle.release;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.gradle.ReleaseNeededTask;
import org.shipkit.internal.comparison.PublicationsComparatorTask;
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitBranchPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;

/**
 * Adds tasks for checking if release is needed
 *
 * Applies following plugins and preconfigures tasks provided by those plugins:
 *
 * <ul>
 *     <li>{@link ReleaseConfigurationPlugin}</li>
 *     <li>{@link GitBranchPlugin}</li>
 * </ul>
 *
 * Adds following tasks:
 *
 * <ul>
 *     <li>assertReleaseNeeded - {@link ReleaseNeededTask}</li>
 *     <li>releaseNeeded - {@link ReleaseNeededTask}</li>
 * </ul>
 */
public class ReleaseNeededPlugin implements Plugin<Project> {

    public final static String ASSERT_RELEASE_NEEDED_TASK = "assertReleaseNeeded";
    public final static String RELEASE_NEEDED = "releaseNeeded";

    @Override
    public void apply(Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        //Task that throws an exception when release is not needed is very useful for CI workflows
        //Travis CI job will stop executing further commands if assertReleaseNeeded fails.
        //See the example projects how we have set up the 'assertReleaseNeeded' task in CI pipeline.
        releaseNeededTask(project, ASSERT_RELEASE_NEEDED_TASK, conf)
                .setExplosive(true)
                .setDescription("Asserts that criteria for the release are met and throws exception if release is not needed.");

        //Below task is useful for testing. It will not throw an exception but will run the code that check is release is needed
        //and it will print the information to the console.
        releaseNeededTask(project, RELEASE_NEEDED, conf)
                .setExplosive(false)
                .setDescription("Checks and prints to the console if criteria for the release are met.");
    }

    private static ReleaseNeededTask releaseNeededTask(final Project project, String taskName,
                                                       final ReleaseConfiguration conf) {
        return TaskMaker.task(project, taskName, ReleaseNeededTask.class, new Action<ReleaseNeededTask>() {
            public void execute(final ReleaseNeededTask t) {
                t.setDescription("Asserts that criteria for the release are met and throws exception if release not needed.");
                t.setExplosive(true);

                project.allprojects(new Action<Project>() {
                    public void execute(final Project subproject) {
                        subproject.getPlugins().withType(PublicationsComparatorPlugin.class, new Action<PublicationsComparatorPlugin>() {
                            public void execute(PublicationsComparatorPlugin p) {
                                // make this task depend on all comparePublications tasks
                                Task task = subproject.getTasks().getByName(PublicationsComparatorPlugin.COMPARE_PUBLICATIONS_TASK);
                                t.addPublicationsComparator((PublicationsComparatorTask) task);
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

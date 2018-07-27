package org.shipkit.internal.gradle.release;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.StopExecutionException;
import org.shipkit.gradle.exec.ShipkitExecTask;
import org.shipkit.internal.gradle.git.GitSetupPlugin;
import org.shipkit.internal.gradle.release.tasks.ReleaseNeeded;
import org.shipkit.internal.gradle.util.GradleWrapper;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.gradle.util.TaskSuccessfulMessage;

import java.io.File;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.exec.ExecCommandFactory.execCommand;

/**
 * Releasing from continuous integration (CI) builds.
 * Intended for root project.
 * <p>
 * Applies:
 * <ul>
 *     <li>{@link ReleasePlugin}</li>
 *     <li>{@link GitSetupPlugin}</li>
 * </ul>
 * Adds tasks:
 * <ul>
 *     <li>ciPerformRelease ({@link ShipkitExecTask})
 *     - convenience task to execute release using a single Gradle task in ci build</li>
 * </ul>
 */
public class CiReleasePlugin implements Plugin<Project> {

    public static final String CI_PERFORM_RELEASE_TASK = "ciPerformRelease";

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(ReleasePlugin.class);
        project.getPlugins().apply(GitSetupPlugin.class);

        /*
        Gradle task model does not make it easy to model releasing scenarios
          therefore we are forking invocations of Gradle tasks from within Gradle task.
        More details:
          We need to stop executing the release if it is not needed. Modelling this using standard task dependencies is not viable.
          We would have to make all tasks depend on 'release needed', which would cause this task to be executed every time we run any task.
          That does not make sense: you run "./gradlew clean" and Gradle would trigger 'releaseNeeded' task.
          Also, when release is not needed, we don't have clean Gradle API to stop the build, without failing it.
          Hence, we are pragmatic. We are forking Gradle from Gradle which seems hacky but we have no other viable choice.
        */
        TaskMaker.task(project, CI_PERFORM_RELEASE_TASK, ShipkitExecTask.class, new Action<ShipkitExecTask>() {
            @Override
            public void execute(ShipkitExecTask task) {
                task.setDescription("Checks if release is needed. If so it will prepare for ci release and perform release.");
                task.getExecCommands().add(execCommand(
                    "Checking if release is needed", asList(GradleWrapper.getWrapperCommand(), ReleaseNeededPlugin.RELEASE_NEEDED), execResult -> {
                        if (!new File(project.getBuildDir(), ReleaseNeeded.RELEASE_NEEDED_FILENAME).exists()) {
                            throw new StopExecutionException();
                        }
                    }));
                task.getExecCommands().add(execCommand(
                        "Preparing working copy for the release", asList(GradleWrapper.getWrapperCommand(), GitSetupPlugin.CI_RELEASE_PREPARE_TASK)));
                task.getExecCommands().add(execCommand(
                        "Performing the release", asList(GradleWrapper.getWrapperCommand(), ReleasePlugin.PERFORM_RELEASE_TASK)));

                TaskSuccessfulMessage.logOnSuccess(task, "  Release " + project.getVersion() + " was shipped! Thank you for using Shipkit!");
            }
        });
    }
}

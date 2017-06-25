package org.shipkit.internal.gradle.release;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.process.ExecResult;
import org.shipkit.gradle.exec.CompositeExecTask;
import org.shipkit.gradle.exec.ExecCommand;
import org.shipkit.internal.gradle.GitSetupPlugin;
import org.shipkit.internal.gradle.ReleaseNeededPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.GitSetupPlugin.CI_RELEASE_PREPARE_TASK;
import static org.shipkit.internal.gradle.ReleaseNeededPlugin.ASSERT_RELEASE_NEEDED_TASK;
import static org.shipkit.internal.gradle.release.ReleasePlugin.PERFORM_RELEASE_TASK;

/**
 * Adds convenience 'ciPerformRelease' task to execute release using a single Gradle task.
 */
public class CiReleasePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(ReleasePlugin.class);
        project.getPlugins().apply(ReleaseNeededPlugin.class);
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
        TaskMaker.task(project, "ciPerformRelease", CompositeExecTask.class, new Action<CompositeExecTask>() {
            @Override
            public void execute(CompositeExecTask task) {
                task.setDescription("Checks if release is needed. If so it will prepare for ci release and perform release.");
                task.getExecCommands().add(new ExecCommand(asList("./gradlew", ASSERT_RELEASE_NEEDED_TASK), stopExecution()));
                task.getExecCommands().add(new ExecCommand(asList("./gradlew", CI_RELEASE_PREPARE_TASK)));
                task.getExecCommands().add(new ExecCommand(asList("./gradlew", PERFORM_RELEASE_TASK, "-Pshipkit.dryRun=false")));
            }
        });
    }

    private Action<ExecResult> stopExecution() {
        return new Action<ExecResult>() {
            public void execute(ExecResult exec) {
                if (exec.getExitValue() != 0) {
                    //Cleanly stop executing the task, without making the task failed.
                    throw new StopExecutionException();
                }
            }
        };
    }
}

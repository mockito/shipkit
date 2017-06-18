package org.shipkit.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.InitTravisTask;
import org.shipkit.internal.gradle.util.TaskMaker;

import java.io.File;

/**
 * Creates task initShipkit that all other init tasks should depend on
 * so that running it would create all configuration needed to start Shipkit
 */
public class InitPlugin implements Plugin<Project> {

    public static final String INIT_SHIPKIT_TASK = "initShipkit";
    public static final String INIT_TRAVIS_TASK = "initTravis";

    @Override
    public void apply(final Project project) {
        TaskMaker.task(project, INIT_TRAVIS_TASK, InitTravisTask.class, new Action<InitTravisTask>() {
            public void execute(InitTravisTask t) {
                t.setDescription("Creates '.travis.yml' file if not already present.");
                t.setOutputFile(new File(project.getRootDir(), ".travis.yml"));
            }
        });

        TaskMaker.task(project, INIT_SHIPKIT_TASK, new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Initializes Shipkit");
                t.dependsOn(INIT_TRAVIS_TASK);
            }
        });

    }
}

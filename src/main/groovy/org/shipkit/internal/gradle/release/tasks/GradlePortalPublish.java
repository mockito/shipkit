package org.shipkit.internal.gradle.release.tasks;

import org.shipkit.gradle.release.GradlePortalPublishTask;
import org.shipkit.internal.exec.DefaultProcessRunner;

import static java.util.Arrays.asList;

public class GradlePortalPublish {

    public void publishPlugins(GradlePortalPublishTask task) {
        DefaultProcessRunner runner =
                new DefaultProcessRunner(task.getProject().getProjectDir())
                        .setSecretValues(asList(task.getPublishKey(), task.getPublishSecret()));

        runner.run(asList("./gradlew", "publishPlugins",
                "-Pgradle.publish.key=" + task.getPublishKey(),
                "-Pgradle.publish.secret=" + task.getPublishSecret()));
    }
}

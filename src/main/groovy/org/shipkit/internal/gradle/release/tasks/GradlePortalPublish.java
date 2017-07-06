package org.shipkit.internal.gradle.release.tasks;

import org.shipkit.gradle.release.GradlePortalPublishTask;
import org.shipkit.internal.gradle.exec.ShipkitExec;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.exec.ExecCommandFactory.execCommand;

public class GradlePortalPublish {

    public void publishPlugins(GradlePortalPublishTask task) {
        new ShipkitExec().execCommands(asList(execCommand(
                "Publishing to http://plugins.gradle.org",
                asList("./gradlew", "publishPlugins",
                        "-Pgradle.publish.key=" + task.getPublishKey(),
                        "-Pgradle.publish.secret=" + task.getPublishSecret())
        )), task.getProject());
    }
}

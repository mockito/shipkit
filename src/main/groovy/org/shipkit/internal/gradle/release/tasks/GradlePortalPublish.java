package org.shipkit.internal.gradle.release.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.release.GradlePortalPublishTask;
import org.shipkit.internal.gradle.exec.ShipkitExec;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.exec.ExecCommandFactory.execCommand;

public class GradlePortalPublish {

    private final static Logger LOG = Logging.getLogger(GradlePortalPublish.class);

    //TODO SF unit test
    public void publishPlugins(GradlePortalPublishTask task) {
        if (task.isDryRun()) {
            LOG.lifecycle("{} - dry run is enabled, skipping publication to http://plugins.gradle.org");
        } else {
            new ShipkitExec().execCommands(asList(execCommand(
                    "Publishing to http://plugins.gradle.org",
                    asList("./gradlew", "publishPlugins",
                            "-Pgradle.publish.key=" + task.getPublishKey(),
                            "-Pgradle.publish.secret=" + task.getPublishSecret())
            )), task.getProject());
        }
    }
}

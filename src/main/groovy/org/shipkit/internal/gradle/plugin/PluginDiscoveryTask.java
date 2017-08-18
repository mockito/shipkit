package org.shipkit.internal.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class PluginDiscoveryTask extends DefaultTask {

    @TaskAction
    public void discover() {
        new PluginDiscovery().discover(getProject());
    }

}

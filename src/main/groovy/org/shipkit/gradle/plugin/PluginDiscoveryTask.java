package org.shipkit.gradle.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.plugin.tasks.PluginDiscovery;

public class PluginDiscoveryTask extends DefaultTask {

    @TaskAction
    public void discover() {
        new PluginDiscovery().discover(getProject());
    }

}

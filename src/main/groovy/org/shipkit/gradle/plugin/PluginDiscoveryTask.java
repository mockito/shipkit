package org.shipkit.gradle.plugin;

import com.gradle.publish.PluginBundleExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.plugin.tasks.PluginDiscovery;

/**
 * Discovers gradle plugins and adds them to the {@link PluginBundleExtension}.
 */
public class PluginDiscoveryTask extends DefaultTask {

    @TaskAction
    public void discover() {
        new PluginDiscovery().discover(getProject());
    }

}

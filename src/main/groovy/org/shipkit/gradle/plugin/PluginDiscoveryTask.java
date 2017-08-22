package org.shipkit.gradle.plugin;

import com.gradle.publish.PluginBundleExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.plugin.tasks.PluginDiscovery;

/**
 * Discovers gradle plugins and adds them to the {@link PluginBundleExtension}.
 * <p>
 * This task will automatically pick up available gradle plugins (discovered via properties files in
 * META-INF/gradle-plugins) during execution time and will configure the pluginBundle extension (provided via
 * 'com.gradle.plugin-publish' plugin) accordingly.
 */
public class PluginDiscoveryTask extends DefaultTask {

    @TaskAction
    public void discover() {
        new PluginDiscovery().discover(getProject());
    }

}

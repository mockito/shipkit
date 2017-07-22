package org.shipkit.internal.gradle.plugin;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.File;
import java.util.Set;

import static org.shipkit.internal.gradle.plugin.PluginDiscoveryPlugin.DOT_PROPERTIES;


public class PluginValidator {

    private static final Logger LOG = Logging.getLogger(PluginValidator.class);

    public void validate(Set<File> gradlePlugins, Set<File> gradleProperties) {
        ensurePluginsHavePropertiesFile(gradlePlugins, gradleProperties);
    }

    void ensurePluginsHavePropertiesFile(Set<File> gradlePlugins, Set<File> gradleProperties) {
        for (File plugin : gradlePlugins) {
            String pluginFilename = plugin.getName();
            String pluginName = extractPluginName(pluginFilename);
            if (pluginName != null) {
                String className = extractClassName(pluginName);
                boolean matchingPropertiesFound = false;
                for (File properties : gradleProperties) {
                    int lastIndexOfProperties = properties.getName().lastIndexOf(DOT_PROPERTIES);
                    if (lastIndexOfProperties != -1) {
                        String pluginExtractedFromProperties = properties.getName().substring(0, lastIndexOfProperties);
                        if (pluginExtractedFromProperties.toLowerCase().endsWith(className.toLowerCase())) {
                            matchingPropertiesFound = true;
                            LOG.info("plugin " + plugin + " has properties file " + gradleProperties);
                            break;
                        }
                    }
                }
                if (!matchingPropertiesFound) {
                    throw new RuntimeException("no properties file found for plugin '" + pluginName + "' (" + plugin + ")");
                }
            }
        }
    }

    private String extractClassName(String pluginName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pluginName.length(); i++) {
            char c = pluginName.charAt(i);
            if (Character.isUpperCase(c)) {
                if (sb.length() != 0) {
                    sb.append("-");
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private String extractPluginName(String pluginFilename) {
        String pluginName = null;
        if (pluginFilename.endsWith("Plugin.java") || pluginFilename.endsWith("Plugin.groovy")) {
            pluginName = pluginFilename.substring(0, pluginFilename.lastIndexOf("Plugin."));
        }
        return pluginName;
    }
}

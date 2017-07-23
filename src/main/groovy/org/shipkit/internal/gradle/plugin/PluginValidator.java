package org.shipkit.internal.gradle.plugin;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.File;
import java.util.*;

import static org.shipkit.internal.gradle.plugin.PluginUtil.DOT_PROPERTIES;


public class PluginValidator {

    private static final Logger LOG = Logging.getLogger(PluginValidator.class);

    public void validate(Set<File> gradlePlugins, Set<File> gradleProperties) {
        ensurePluginsHavePropertiesFile(gradlePlugins, gradleProperties);
    }

    private void ensurePluginsHavePropertiesFile(Set<File> gradlePlugins, Set<File> gradleProperties) {
        Map<String, File> missingPropertiesFiles = new HashMap<String, File>();
        for (File plugin : gradlePlugins) {
            String pluginFilename = plugin.getName();
            String pluginName = extractPluginName(pluginFilename);
            if (pluginName != null) {
                String convertedPropertiesFleName = convertToPropertiesFileName(pluginName);
                boolean matchingPropertiesFound = false;
                for (File properties : gradleProperties) {
                    int lastIndexOfProperties = properties.getName().lastIndexOf(DOT_PROPERTIES);
                    if (lastIndexOfProperties != -1) {
                        String pluginExtractedFromProperties = properties.getName().substring(0, lastIndexOfProperties);
                        if (pluginExtractedFromProperties.toLowerCase().endsWith(convertedPropertiesFleName.toLowerCase())) {
                            matchingPropertiesFound = true;
                            LOG.info("plugin " + plugin + " has properties file " + gradleProperties);
                            break;
                        }
                    }
                }
                if (!matchingPropertiesFound) {
                    missingPropertiesFiles.put(pluginName, plugin);
                }
            }
        }

        throwExceptionIfNeeded(missingPropertiesFiles);
    }

    private void throwExceptionIfNeeded(Map<String, File> missingPropertiesFiles) {
        if (missingPropertiesFiles.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("no properties file found for plugin(s):");
            for (String pluginName : missingPropertiesFiles.keySet()) {
                sb.append("\n\t");
                sb.append("'");
                sb.append(pluginName);
                sb.append("' (");
                sb.append(missingPropertiesFiles.get(pluginName));
                sb.append(")");
            }
            throw new RuntimeException(sb.toString());
        }
    }

    /**
     * Converts a given plugin name to the corresponding properties file name.
     *
     * e.g. 'PluginDiscovery' will return 'plugin-discovery'
     *
     * @param pluginName the plugin name to convert to a corresponding properties file name.
     * @return the corresponding properties file name
     */
    private String convertToPropertiesFileName(String pluginName) {
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
        return sb.toString().toLowerCase();
    }

    private String extractPluginName(String pluginFilename) {
        String pluginName = null;
        if (pluginFilename.endsWith("Plugin.java") || pluginFilename.endsWith("Plugin.groovy")) {
            pluginName = pluginFilename.substring(0, pluginFilename.lastIndexOf("Plugin."));
        }
        return pluginName;
    }
}

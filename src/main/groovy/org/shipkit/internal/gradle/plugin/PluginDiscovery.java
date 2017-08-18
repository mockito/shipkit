package org.shipkit.internal.gradle.plugin;

import com.gradle.publish.PluginBundleExtension;
import com.gradle.publish.PluginConfig;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import static org.shipkit.internal.gradle.plugin.PluginUtil.DOT_PROPERTIES;

public class PluginDiscovery {

    private static Logger LOG = Logging.getLogger(PluginDiscovery.class);

    public void discover(Project project) {
        PluginBundleExtension extension = project.getExtensions().findByType(PluginBundleExtension.class);

        Set<File> pluginPropertyFiles = PluginUtil.discoverGradlePluginPropertyFiles(project);
        LOG.lifecycle("  Adding {} discovered Gradle plugins to 'pluginBundle'", pluginPropertyFiles.size());
        for (File pluginPropertyFile : pluginPropertyFiles) {
            PluginConfig config = new PluginConfig(generatePluginName(pluginPropertyFile.getName()));
            config.setId(pluginPropertyFile.getName().substring(0, pluginPropertyFile.getName().lastIndexOf(DOT_PROPERTIES)));
            config.setDisplayName(getImplementationClass(pluginPropertyFile));
            LOG.info("Discovered plugin " + config);
            extension.getPlugins().add(config);
        }
    }

    static String generatePluginName(String fileName) {
        String pluginName = fileName.substring(0, fileName.lastIndexOf(DOT_PROPERTIES));
        pluginName = pluginName.substring(pluginName.lastIndexOf(".") + 1);
        String[] split = pluginName.split("-");
        StringBuilder sb = new StringBuilder();
        for (String string : split) {
            if(sb.length() == 0) {
                sb.append(string.substring(0, 1).toLowerCase()).append(string.substring(1));
            } else {
                sb.append(string.substring(0, 1).toUpperCase()).append(string.substring(1));
            }
        }
        return sb.toString();
    }

    static String getImplementationClass(File file) {
        Properties properties = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            properties.load(is);
            return properties.getProperty("implementation-class");
        } catch (Exception e) {
            throw new RuntimeException("error while reading " + file, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }
}

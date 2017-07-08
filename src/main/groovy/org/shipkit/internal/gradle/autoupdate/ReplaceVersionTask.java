package org.shipkit.internal.gradle.autoupdate;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;

/**
 * Replaces version of given dependency with {@link ReplaceVersionTask#newVersion}
 * in the given {@link ReplaceVersionTask#configFile}
 * Use {@link ReplaceVersionTask#dependencyPattern} to tell Shipkit how it should
 * replace the dependency. You should set it to regex pattern that contains
 * {@link ReplaceVersionTask.VERSION_PLACEHOLDER} instead of version number.
 */
public class ReplaceVersionTask extends DefaultTask{

    public static final Logger LOG = Logging.getLogger(ReplaceVersionTask.class);

    public static final String VERSION_PLACEHOLDER = "{VERSION}";
    public static final String VERSION_REGEX = "[0-9.]+";

    private String newVersion;
    private File configFile;
    private String dependencyPattern;

    @TaskAction
    public void replaceVersion(){
        LOG.lifecycle("  Replacing version in '{}' using pattern '{}' and version '{}'.", configFile, dependencyPattern, newVersion);
        String versionPattern = dependencyPattern.replace(VERSION_PLACEHOLDER, VERSION_REGEX);
        String replacement = dependencyPattern.replace(VERSION_PLACEHOLDER, newVersion);

        String content = IOUtil.readFully(configFile);
        String updatedContent = content.replaceAll(versionPattern ,replacement);
        IOUtil.writeFile(configFile.getAbsoluteFile(), updatedContent);
    }

    /**
     * New version to which dependency will be updated
     */
    public String getNewVersion() {
        return newVersion;
    }

    /**
     * See {@link #getNewVersion()}
     */
    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    /**
     * Config file where dependency will be updated
     */
    public File getConfigFile() {
        return configFile;
    }

    /**
     * See {@link #getConfigFile()}
     */
    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    /**
     * Regex pattern which is used to replace dependency, should contain {@link VERSION_PLACEHOLDER}
     * instead of version. Eg. "org.shipkit:shipkit:{VERSION}"
     */
    public String getDependencyPattern() {
        return dependencyPattern;
    }

    /**
     * See {@link #getDependencyPattern()}
     */
    public void setDependencyPattern(String dependencyPattern) {
        this.dependencyPattern = dependencyPattern;
    }
}

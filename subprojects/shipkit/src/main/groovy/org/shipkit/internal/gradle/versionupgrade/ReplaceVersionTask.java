package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;

/**
 * Replaces version of given dependency with {@link #getNewVersion()}
 * in the given {@link #getBuildFile()}
 * To replace the dependency this task uses following regex pattern:
 * "{@link #getDependencyGroup()}:{@link #getDependencyName()}:{@value VERSION_REGEX}
 */
public class ReplaceVersionTask extends DefaultTask {

    public static final Logger LOG = Logging.getLogger(ReplaceVersionTask.class);

    public static final String VERSION_REGEX = "[0-9.]+";

    private String dependencyGroup;
    private String dependencyName;
    private String newVersion;
    private File buildFile;

    private Boolean buildFileUpdated;

    @TaskAction
    public void replaceVersion() {
        String groupAndProject = dependencyGroup + ":" + dependencyName + ":";
        String versionPattern = groupAndProject + VERSION_REGEX;
        String replacement = groupAndProject + newVersion;

        LOG.lifecycle("  Replacing version in '{}' using pattern '{}' and version '{}'.", buildFile, versionPattern, newVersion);

        String content = IOUtil.readFully(buildFile);
        String updatedContent = content.replaceAll(versionPattern, replacement);

        buildFileUpdated = !content.equals(updatedContent);

        if (buildFileUpdated) {
            IOUtil.writeFile(buildFile.getAbsoluteFile(), updatedContent);
        }
    }

    /**
     * See {@link UpgradeDependencyExtension#getDependencyGroup()}
     */
    public String getDependencyGroup() {
        return dependencyGroup;
    }

    /**
     * See {@link UpgradeDependencyExtension#getDependencyGroup()}
     */
    public void setDependencyGroup(String dependencyGroup) {
        this.dependencyGroup = dependencyGroup;
    }

    /**
     * See {@link UpgradeDependencyExtension#getDependencyName()}
     */
    public String getDependencyName() {
        return dependencyName;
    }

    /**
     * See {@link UpgradeDependencyExtension#getDependencyName()}
     */
    public void setDependencyName(String dependencyName) {
        this.dependencyName = dependencyName;
    }

    /**
     * See {@link UpgradeDependencyExtension#getNewVersion()}
     */
    public String getNewVersion() {
        return newVersion;
    }

    /**
     * See {@link UpgradeDependencyExtension#getNewVersion()}
     */
    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    /**
     * See {@link UpgradeDependencyExtension#getBuildFile()}
     */
    public File getBuildFile() {
        return buildFile;
    }

    /**
     * See {@link UpgradeDependencyExtension#getBuildFile()}
     */
    public void setBuildFile(File buildFile) {
        this.buildFile = buildFile;
    }

    /**
     * Was buildFile updated during the version replacement?
     * It wasn't when newVersion == previousVersion
     */
    public boolean isBuildFileUpdated() {
        if (buildFileUpdated == null) {
            throw new IllegalStateException("Property 'buildFileUpdated' should not be accessed before 'replaceVersion' task is executed.");
        }
        return buildFileUpdated;
    }
}

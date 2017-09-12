package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.notes.util.IOUtil;

/**
 * Replaces version of given dependency with {@link UpgradeDependencyExtension#newVersion}
 * in the given {@link UpgradeDependencyExtension#buildFile}
 * To replace the dependency this task uses following regex pattern:
 * "{@link UpgradeDependencyExtension#dependencyGroup}:{@link UpgradeDependencyExtension#dependencyName}:{@value VERSION_REGEX}
 */
public class ReplaceVersionTask extends DefaultTask {

    public static final Logger LOG = Logging.getLogger(ReplaceVersionTask.class);

    public static final String VERSION_REGEX = "[0-9.]+";

    private UpgradeDependencyExtension versionUpgrade;
    private Boolean buildFileUpdated;

    @TaskAction
    public void replaceVersion() {
        String groupAndProject = versionUpgrade.getDependencyGroup() + ":" + versionUpgrade.getDependencyName() + ":";
        String versionPattern = groupAndProject + VERSION_REGEX;
        String replacement = groupAndProject + versionUpgrade.getNewVersion();

        LOG.lifecycle("  Replacing version in '{}' using pattern '{}' and version '{}'.", versionUpgrade.getBuildFile(), versionPattern, versionUpgrade.getNewVersion());

        String content = IOUtil.readFully(versionUpgrade.getBuildFile());
        String updatedContent = content.replaceAll(versionPattern, replacement);

        buildFileUpdated = !content.equals(updatedContent);

        if (buildFileUpdated) {
            IOUtil.writeFile(versionUpgrade.getBuildFile().getAbsoluteFile(), updatedContent);
        }
    }

    public UpgradeDependencyExtension getVersionUpgrade() {
        return versionUpgrade;
    }

    public void setVersionUpgrade(UpgradeDependencyExtension versionUpgrade) {
        this.versionUpgrade = versionUpgrade;
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

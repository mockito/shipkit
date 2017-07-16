package org.shipkit.internal.gradle;

import java.io.File;

/**
 * Configuration of {@link VersionUpgradeConsumerPlugin}
 */
public class VersionUpgrade {
    private String baseBranch;
    private File buildFile;
    private String newVersion;
    private String dependencyName;
    private String dependencyGroup;

    /**
     * Base branch to which pull request should be created
     */
    public String getBaseBranch() {
        return baseBranch;
    }

    /**
     * See {@link #getBaseBranch()}
     */
    public void setBaseBranch(String baseBranch) {
        this.baseBranch = baseBranch;
    }

    /**
     * File where dependency's version should be replaced
     */
    public File getBuildFile() {
        return buildFile;
    }

    /**
     * See {@link #getBuildFile()}
     */
    public void setBuildFile(File buildFile) {
        this.buildFile = buildFile;
    }

    /**
     * New version of the dependency to be upgraded, eg. '1.2.3'
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
     * Name of the dependency to be upgraded, eg. 'shipkit'
     */
    public String getDependencyName() {
        return dependencyName;
    }

    /**
     * See {@link #getDependencyName()}
     */
    public void setDependencyName(String dependencyName) {
        this.dependencyName = dependencyName;
    }

    /**
     * Group of the dependency to be upgraded, eg. 'org.shipkit'
     */
    public String getDependencyGroup() {
        return dependencyGroup;
    }

    /**
     * See {@link #getDependencyGroup()}
     */
    public void setDependencyGroup(String dependencyGroup) {
        this.dependencyGroup = dependencyGroup;
    }
}

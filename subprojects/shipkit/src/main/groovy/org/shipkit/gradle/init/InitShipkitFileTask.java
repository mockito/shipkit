package org.shipkit.gradle.init;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.init.tasks.InitShipkitFile;

import java.io.File;

/**
 * Initializes Shipkit configuration file with initial values.
 * Generated file is intended to be checked in.
 * If the file already exists, this task does nothing.
 */
public class InitShipkitFileTask extends DefaultTask {

    private String originRepoName;
    private File shipkitFile;

    /**
     * Initial Shipkit configuration will be generated to this file.
     * If the file exists, the task does nothing.
     */
    public File getShipkitFile() {
        return shipkitFile;
    }

    /**
     * See {@link #getShipkitFile()}
     */
    public void setShipkitFile(File shipkitFile) {
        this.shipkitFile = shipkitFile;
    }

    /**
     * Name of the current git origin repo in the format "user/repo", eg. "mockito/shipkit"
     */
    public String getOriginRepoName() {
        return originRepoName;
    }

    /**
     * See {@link #getOriginRepoName()}
     */
    public void setOriginRepoName(String originRepoName) {
        this.originRepoName = originRepoName;
    }

    @TaskAction
    public void initShipkitFile() {
        new InitShipkitFile().initShipkitFile(this);
    }
}

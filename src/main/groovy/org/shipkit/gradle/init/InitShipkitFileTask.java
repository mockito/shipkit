package org.shipkit.gradle.init;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.init.tasks.InitShipkitFile;

import java.io.File;

/**
 * Initializes Shipkit configuration file with some default values.
 * Generated file is intended to be checked in.
 * If the file already exists, this task does nothing.
 */
public class InitShipkitFileTask extends DefaultTask {

    private String originRepoName;
    private File shipkitFile;

    public File getShipkitFile() {
        return shipkitFile;
    }

    public void setShipkitFile(File shipkitFile) {
        this.shipkitFile = shipkitFile;
    }

    public String getOriginRepoName() {
        return originRepoName;
    }

    public void setOriginRepoName(String originRepoName) {
        this.originRepoName = originRepoName;
    }

    @TaskAction
    public void initShipkitFile() {
        new InitShipkitFile().initShipkitFile(this);
    }
}

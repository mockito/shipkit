package org.shipkit.gradle.java;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.java.tasks.DownloadPreviousPublications;

import java.io.File;

/**
 * Downloads last release source jar given by {@link #getPreviousSourcesJarUrl()}
 * from repositories (eg. Bintray) and stores it to local file, given by {@link #getPreviousSourcesJarFile()}
 * for further comparison.
 *
 * For details of the comparison see {@link ComparePublicationsTask}
 */
public class DownloadPreviousPublicationsTask extends DefaultTask {

    @Input private String previousSourcesJarUrl;

    @OutputFile private File previousSourcesJarFile;

    @TaskAction
    public void downloadPreviousPublications() {
        new DownloadPreviousPublications().downloadPreviousPublications(this);
    }

    /**
     * URL where previous version sources jar can be found
     */
    public String getPreviousSourcesJarUrl() {
        return previousSourcesJarUrl;
    }

    /**
     * See {@link #getPreviousSourcesJarUrl()}
     */
    public void setPreviousSourcesJarUrl(String previousSourcesJarUrl) {
        this.previousSourcesJarUrl = previousSourcesJarUrl;
    }

    /**
     * temporary storage file for downloaded previous version sources jar
     */
    public File getPreviousSourcesJarFile() {
        return previousSourcesJarFile;
    }

    /**
     * See {@link #getPreviousSourcesJarFile()}
     */
    public void setPreviousSourcesJarFile(File previousSourcesJar) {
        this.previousSourcesJarFile = previousSourcesJar;
    }
}

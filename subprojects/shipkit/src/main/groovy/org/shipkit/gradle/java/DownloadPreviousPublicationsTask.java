package org.shipkit.gradle.java;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.java.tasks.DownloadPreviousPublications;

import java.io.File;

/**
 * Downloads artifacts from last release and stores them to local files for further comparison
 * Currently it downloads .pom and -sources.jar
 */
public class DownloadPreviousPublicationsTask extends DefaultTask {

    @Input private String previousSourcesJarUrl;

    @OutputFile private File previousSourcesJar;

    @TaskAction
    public void downloadPreviousPublications() {
        new DownloadPreviousPublications().downloadPreviousPublications(this);
    }

    /**
     * See {@link #setPreviousSourcesJarUrl(String)}
     */
    public String getPreviousSourcesJarUrl() {
        return previousSourcesJarUrl;
    }

    /**
     * @param previousSourcesJarUrl URL where previous version sources jar can be found
     */
    public void setPreviousSourcesJarUrl(String previousSourcesJarUrl) {
        this.previousSourcesJarUrl = previousSourcesJarUrl;
    }

    /**
     * See {@link #setPreviousSourcesJar(File)}
     */
    public File getPreviousSourcesJar() {
        return previousSourcesJar;
    }

    /**
     * @param previousSourcesJar temporary storage file for downloaded previous version sources jar
     */
    public void setPreviousSourcesJar(File previousSourcesJar) {
        this.previousSourcesJar = previousSourcesJar;
    }
}

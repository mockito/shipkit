package org.shipkit.internal.comparison;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;

/**
 * Downloads artifacts from last release and stores them to local files for further comparison
 * Currently it downloads .pom and -sources.jar
 */
public class DownloadPreviousPublicationsTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(DownloadPreviousPublicationsTask.class);

    @Input private String previousPomUrl;
    @Input private String previousSourcesJarUrl;

    @OutputFile private File previousPom;
    @OutputFile private File previousSourcesJar;

    @TaskAction public void downloadPreviousReleases(){
        downloadRemoteFile(previousPomUrl, previousPom);
        downloadRemoteFile(previousSourcesJarUrl, previousSourcesJar);
    }

    private void downloadRemoteFile(String remoteUrl, File localFile) {
        LOG.lifecycle("Downloading remote artifact\n" +
                "  - from {}\n" +
                "  - and saving it to {}", remoteUrl, localFile);

        IOUtil.downloadToFile(remoteUrl, localFile);
    }

    /**
     * See {@link #setPreviousPomUrl(String)}
     */
    public String getPreviousPomUrl() {
        return previousPomUrl;
    }

    /**
     * @param previousPomUrl URL where previous version pom file can be found
     */
    public void setPreviousPomUrl(String previousPomUrl) {
        this.previousPomUrl = previousPomUrl;
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
     * @param previousPom temporary storage file for downloaded previous version pom
     */
    public void setPreviousPom(File previousPom) {
        this.previousPom = previousPom;
    }

    /**
     * See {@link #setPreviousPom(File)}
     */
    public File getPreviousPom() {
        return previousPom;
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

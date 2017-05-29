package org.shipkit.internal.comparison;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.*;
import org.shipkit.notes.util.IOUtil;

import java.io.File;

/**
 * Downloads artifacts from last release and stores them to local files for further comparison
 * Currently it downloads .pom and -sources.jar
 */
public class DownloadPreviousReleaseArtifactsTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(DownloadPreviousReleaseArtifactsTask.class);

    @Input
    private String previousVersionPomUrl;
    @Input
    private String previousVersionSourcesJarUrl;

    @OutputFile
    private File previousVersionPomLocalFile;
    @OutputFile
    private File previousVersionSourcesJarLocalFile;


    @TaskAction public void downloadPreviousReleases(){
        downloadRemoteFile(previousVersionPomUrl, previousVersionPomLocalFile);
        downloadRemoteFile(previousVersionSourcesJarUrl, previousVersionSourcesJarLocalFile);
    }

    private void downloadRemoteFile(String remoteUrl, File localFile) {
        LOG.lifecycle("Downloading remote artifact\n" +
                "  - from {}\n" +
                "  - and saving it to {}", remoteUrl, localFile);

        IOUtil.downloadToFile(remoteUrl, localFile);
    }

    public String getPreviousVersionPomUrl() {
        return previousVersionPomUrl;
    }

    /**
     *
     * @param previousVersionPomUrl URL where previous version pom file can be found
     */
    public void setPreviousVersionPomUrl(String previousVersionPomUrl) {
        this.previousVersionPomUrl = previousVersionPomUrl;
    }

    public String getPreviousVersionSourcesJarUrl() {
        return previousVersionSourcesJarUrl;
    }

    /**
     * @param previousVersionSourcesJarUrl URL where previous version sources jar can be found
     */
    public void setPreviousVersionSourcesJarUrl(String previousVersionSourcesJarUrl) {
        this.previousVersionSourcesJarUrl = previousVersionSourcesJarUrl;
    }

    /**
     * @param previousVersionPomLocalFile temporary storage file for downloaded previous version pom
     */
    public void setPreviousVersionPomLocalFile(File previousVersionPomLocalFile) {
        this.previousVersionPomLocalFile = previousVersionPomLocalFile;
    }

    public File getPreviousVersionPomLocalFile() {
        return previousVersionPomLocalFile;
    }

    public File getPreviousVersionSourcesJarLocalFile() {
        return previousVersionSourcesJarLocalFile;
    }

    /**
     * @param previousVersionSourcesJarLocalFile temporary storage file for downloaded previous version sources jar
     */
    public void setPreviousVersionSourcesJarLocalFile(File previousVersionSourcesJarLocalFile) {
        this.previousVersionSourcesJarLocalFile = previousVersionSourcesJarLocalFile;
    }
}

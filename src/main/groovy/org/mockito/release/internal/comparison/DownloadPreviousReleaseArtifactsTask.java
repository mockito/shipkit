package org.mockito.release.internal.comparison;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.*;
import org.mockito.release.notes.util.IOUtil;

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
        if(previousVersionPomUrl == null || previousVersionSourcesJarUrl == null){
            throw new GradleException("You have to configure previousVersionPomUrl and previousVersionSourcesJarUrl to use DownloadPreviousReleaseArtifactsTask.\n"
                    + "If you use one of the supported publishing plugins default url will be configured for you.\n"
                    + "Currently supported plugins: Bintray"
            );
        }
        downloadRemoteFile(".pom", previousVersionPomUrl, previousVersionPomLocalFile);
        downloadRemoteFile("-sources.jar", previousVersionSourcesJarUrl, previousVersionSourcesJarLocalFile);
    }

    private void downloadRemoteFile(String extension, String remoteUrl, File localFile) {
        LOG.lifecycle("Downloading remote artifact\n" +
                "  - from {}\n" +
                "  - and saving it to {}", remoteUrl, localFile);


        IOUtil.downloadToFile(remoteUrl, localFile);
    }

    public String getPreviousVersionPomUrl() {
        return previousVersionPomUrl;
    }

    public void setPreviousVersionPomUrl(String previousVersionPomUrl) {
        this.previousVersionPomUrl = previousVersionPomUrl;
    }

    public String getPreviousVersionSourcesJarUrl() {
        return previousVersionSourcesJarUrl;
    }

    public void setPreviousVersionSourcesJarUrl(String previousVersionSourcesJarUrl) {
        this.previousVersionSourcesJarUrl = previousVersionSourcesJarUrl;
    }

    public void setPreviousVersionPomLocalFile(File previousVersionPomLocalFile) {
        this.previousVersionPomLocalFile = previousVersionPomLocalFile;
    }

    public File getPreviousVersionPomLocalFile() {
        return previousVersionPomLocalFile;
    }

    public File getPreviousVersionSourcesJarLocalFile() {
        return previousVersionSourcesJarLocalFile;
    }

    public void setPreviousVersionSourcesJarLocalFile(File previousVersionSourcesJarLocalFile) {
        this.previousVersionSourcesJarLocalFile = previousVersionSourcesJarLocalFile;
    }
}

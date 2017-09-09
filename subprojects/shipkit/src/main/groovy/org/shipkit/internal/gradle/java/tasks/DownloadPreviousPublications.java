package org.shipkit.internal.gradle.java.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.java.DownloadPreviousPublicationsTask;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;

public class DownloadPreviousPublications {

    private static final Logger LOG = Logging.getLogger(DownloadPreviousPublications.class);

    public void downloadPreviousPublications(DownloadPreviousPublicationsTask task) {
        downloadRemoteFile(task.getPreviousPomUrl(), task.getPreviousPom());
        downloadRemoteFile(task.getPreviousSourcesJarUrl(), task.getPreviousSourcesJar());
    }

    private void downloadRemoteFile(String remoteUrl, File localFile) {
        LOG.lifecycle("  Downloading remote artifact\n" +
                "  - from {}\n" +
                "  - and saving it to {}", remoteUrl, localFile);

        try {
            IOUtil.downloadToFile(remoteUrl, localFile);
        } catch (Exception e) {
            LOG.lifecycle("  Unable to download, ignoring. Run with '-d' for stack trace. Url: {}", remoteUrl);
            LOG.debug("Unable to download, ignoring", e);
        }
    }
}

package org.shipkit.internal.gradle.release.tasks;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.notes.util.IOUtil;
import org.shipkit.internal.util.GitHubApi;

import java.io.File;

public class UploadGists {

    private static final Logger LOG = Logging.getLogger(UploadGistsTask.class);

    public void uploadGists(UploadGistsTask uploadGistsTask) {
        GitHubApi gitHubApi = new GitHubApi(uploadGistsTask.getGitHubApiUrl(), uploadGistsTask.getGitHubWriteToken());
        uploadGists(uploadGistsTask, new GistsApi(gitHubApi));
    }

    public void uploadGists(UploadGistsTask uploadGistsTask, GistsApi gistsApi) {
        boolean oneOfTheUploadsFailed = false;
        for (File file : uploadGistsTask.getFilesToUpload()) {
            try {
                String url = gistsApi.uploadFile(file.getName(), IOUtil.readFully(file));
                LOG.lifecycle("Gist for file '{}' created. You can find it here: {}", file.getAbsolutePath(), url);
            } catch (Exception e) {
                LOG.error("Creating a Gist for '" + file.getAbsolutePath() + "' failed.", e);
                oneOfTheUploadsFailed = true;
            }
        }
        if (oneOfTheUploadsFailed) {
            throw new GradleException("Uploading one of the Gists failed. See the logs above for the details.");
        }
    }

}

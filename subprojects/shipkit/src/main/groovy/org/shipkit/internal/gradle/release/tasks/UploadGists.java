package org.shipkit.internal.gradle.release.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.gradle.util.FileUtil;
import org.shipkit.internal.notes.util.IOUtil;
import org.shipkit.internal.util.GitHubApi;

import java.io.File;

public class UploadGists {

    private static final Logger LOG = Logging.getLogger(UploadGistsTask.class);

    public void uploadGists(UploadGistsTask uploadGistsTask) {
        uploadGists(uploadGistsTask, new GitHubApi(uploadGistsTask.getGitHubApiUrl(), uploadGistsTask.getGitHubWriteToken()));
    }

    public void uploadGists(UploadGistsTask uploadGistsTask, GitHubApi gitHubApi) {
        for (String logFilePattern : uploadGistsTask.getFilesPatterns()) {
            uploadForPattern(logFilePattern, gitHubApi, uploadGistsTask.getRootDir());
        }
    }

    private void uploadForPattern(final String logFilePattern, GitHubApi gitHubApi, String rootDir) {
        for (String logFile : FileUtil.findFilesByPattern(rootDir, logFilePattern)) {
            uploadSingleFile(logFile, gitHubApi);
        }
    }

    private void uploadSingleFile(String filePath, GitHubApi gitHubApi) {
        File file = new File(filePath);
        String body = getBody(file);

        try {
            String response = gitHubApi.post("/gists", body);
            JsonObject responseJson = (JsonObject) Jsoner.deserialize(response);
            String url = responseJson.getString("html_url");
            LOG.lifecycle("Gist for file '{}' created. You can find it here: {}", filePath, url);
        } catch (Exception e) {
            LOG.error("Failed to upload log file " + filePath + " to Gist.", e);
            throw new RuntimeException(e);
        }
    }

    private String getBody(File file) {
        String fileContent = IOUtil.readFully(file);
        String fileName = file.getName();

        JsonObject files = new JsonObject();
        JsonObject singleFile = new JsonObject();
        singleFile.put("content", fileContent);
        files.put(fileName, singleFile);

        JsonObject body = new JsonObject();
        body.put("files", files);
        body.put("public", "true");
        body.put("description", file.getName());

        return body.toJson();
    }
}

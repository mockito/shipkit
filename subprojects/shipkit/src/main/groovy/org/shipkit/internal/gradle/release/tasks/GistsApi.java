package org.shipkit.internal.gradle.release.tasks;

import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.util.GitHubApi;

public class GistsApi {

    private final GitHubApi gitHubApi;

    public GistsApi(GitHubApi gitHubApi) {
        this.gitHubApi = gitHubApi;
    }

    /**
     * Creates a Gist with the given fileContent and uploads it.
     * Returns the url that you can use to access the uploaded Gist.
     *
     * @param fileContent the content which will be uploaded
     */
    public String uploadFile(String fileName, String fileContent) throws Exception {
        String body = getBody(fileName, fileContent);

        String response = gitHubApi.post("/gists", body);
        JsonObject responseJson = (JsonObject) Jsoner.deserialize(response);

        return responseJson.getString("html_url");
    }

    private String getBody(String fileName, String fileContent) {
        JsonObject files = new JsonObject();
        JsonObject singleFile = new JsonObject();
        singleFile.put("content", fileContent);
        files.put(fileName, singleFile);

        JsonObject body = new JsonObject();
        body.put("files", files);
        body.put("public", "true");
        body.put("description", fileName);

        return body.toJson();
    }
}

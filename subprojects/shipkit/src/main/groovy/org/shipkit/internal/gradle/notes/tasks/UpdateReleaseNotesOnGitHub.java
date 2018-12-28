package org.shipkit.internal.gradle.notes.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.gradle.notes.UpdateReleaseNotesOnGitHubTask;
import org.shipkit.internal.notes.header.HeaderProvider;
import org.shipkit.internal.util.GitHubApi;

public class UpdateReleaseNotesOnGitHub {
    private static final Logger LOG = Logging.getLogger(UpdateReleaseNotesOnGitHub.class);

    public void updateReleaseNotes(UpdateReleaseNotesOnGitHubTask task, HeaderProvider headerProvider) throws Exception {
        String releaseNotesText = new UpdateReleaseNotes().generateNewContent(task, headerProvider);
        updateOnGitHub(task, releaseNotesText);
    }

    private void updateOnGitHub(UpdateReleaseNotesOnGitHubTask task, String text) throws Exception {
        GitHubApi gitHubApi = new GitHubApi(task.getGitHubApiUrl(), task.getGitHubWriteToken());

        JsonObject body = new JsonObject();

        body.put("tag_name", task.getTagPrefix() + task.getVersion());
        body.put("name", task.getTagPrefix() + task.getVersion());
        body.put("body", text);
        body.put("draft", false);
        body.put("prerelease", task.isEmphasizeVersion());

        String url = "/repos/" + task.getUpstreamRepositoryName() + "/releases";

        LOG.debug("GitHub update release notes on release page POST {} body: {}", url, body.toJson());
        LOG.lifecycle("POST {}", url);
        String response = gitHubApi.post(url, body.toJson());

        JsonObject responseJson = (JsonObject) Jsoner.deserialize(response);

        String htmlUrl = responseJson.getString("html_url");
        LOG.lifecycle("  Release notes updated on GitHub: {}", htmlUrl);
    }
}

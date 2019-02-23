package org.shipkit.internal.gradle.notes.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.gradle.notes.UpdateReleaseNotesOnGitHubCleanupTask;
import org.shipkit.gradle.notes.UpdateReleaseNotesOnGitHubTask;
import org.shipkit.internal.gradle.util.StringUtil;
import org.shipkit.internal.notes.header.HeaderProvider;
import org.shipkit.internal.util.GitHubApi;

import java.io.IOException;

public class UpdateReleaseNotesOnGitHub {
    private static final Logger LOG = Logging.getLogger(UpdateReleaseNotesOnGitHub.class);

    private final GitHubApi gitHubApi;
    private final UpdateReleaseNotes updateReleaseNotes;

    public UpdateReleaseNotesOnGitHub(GitHubApi gitHubApi, UpdateReleaseNotes updateReleaseNotes) {
        this.gitHubApi = gitHubApi;
        this.updateReleaseNotes = updateReleaseNotes;
    }

    public void updateReleaseNotes(UpdateReleaseNotesOnGitHubTask task, HeaderProvider headerProvider) throws Exception {
        String releaseNotesText = updateReleaseNotes.generateNewContent(task, headerProvider);
        if (task.isPreviewMode()) {
            LOG.lifecycle("  Preview of release notes update:\n" +
                "  ----------------\n" + releaseNotesText + "\n----------------");
        } else {
            createReleaseByTagName(task, releaseNotesText);
        }
    }

    private String findReleaseByTagName(UpdateReleaseNotesOnGitHubTask task) throws Exception {
        String tagName = tagName(task);
        String url = "/repos/" + task.getUpstreamRepositoryName() + "/releases/tags/" + tagName;
        LOG.lifecycle("GET {}", url);

        try {
            String response = gitHubApi.get(url);
            JsonObject responseJson = (JsonObject) Jsoner.deserialize(response);
            return responseJson.getString("id");
        } catch (Exception e) {
            if (task.isDryRun()) {
                LOG.lifecycle("  returned some error, but run in -PdryRun mode, so will continue with default value: " +
                    "\"DEFAULT_RELEASE_ID\".\nSee stacktrace for more details.", e);
                return "DEFAULT_RELEASE_ID";
            }
            throw e;
        }
    }

    private void createReleaseByTagName(UpdateReleaseNotesOnGitHubTask task, String text) throws Exception {
        String tagName = tagName(task);
        String url = "/repos/" + task.getUpstreamRepositoryName() + "/releases";

        if (task.isDryRun()) {
            LOG.lifecycle("It's -PdryRun mode, releases notes on GitHub will be NOT created." +
                "\n  It will execute in normal mode: POST {}", url);
            return;
        }

        JsonObject body = new JsonObject();
        body.put("tag_name", tagName);
        body.put("name", tagName);
        body.put("body", text);

        LOG.lifecycle("POST {}", url);

        try {
            String response = gitHubApi.post(url, body.toJson());
            JsonObject responseJson = (JsonObject) Jsoner.deserialize(response);

            String htmlUrl = responseJson.getString("html_url");
            LOG.lifecycle("  Successfully updated release notes on GitHub: {}", htmlUrl);
        } catch (Exception e) {
            if (task.isDryRun()) {
                LOG.lifecycle("  returned some error, but run in -PdryRun mode, so will continue with default value: " +
                    "\"DEFAULT_RELEASE_ID\".\nSee stacktrace for more details.", e);
            }
            throw e;
        }
    }

    private String tagName(UpdateReleaseNotesOnGitHubTask task) {
        if (StringUtil.isEmpty(task.getTagPrefix())) {
            return task.getVersion();
        }
        return task.getTagPrefix() + task.getVersion();
    }

    public void removeReleaseNotes(UpdateReleaseNotesOnGitHubCleanupTask task) {
        String releaseId;
        try {
            releaseId = findReleaseByTagName(task);
        } catch (Exception e) {
            LOG.lifecycle("Can't find release on GitHub to remove");
            return;
        }

        try {
            removeRelease(releaseId, task);
        } catch (IOException e) {
            LOG.lifecycle("Can't delete release {} from GitHub", releaseId);
        }
    }

    private void removeRelease(String releaseId, UpdateReleaseNotesOnGitHubCleanupTask task) throws IOException {
        String url = "/repos/" + task.getUpstreamRepositoryName() + "/releases/" + releaseId;
        gitHubApi.delete(url);
    }
}

package org.mockito.release.notes.contributors;

import org.json.simple.JSONObject;
import org.mockito.release.notes.model.Contributor;

import java.util.Map;

/**
 * Provides means to parse JSONObjects returned from calling GitHub API.
 */
public class GitHubCommitsJSON {

    /**
     * Parses GitHub JSONObject in accordance to the API (https://developer.github.com/v3/repos/commits)
     */
    static Contributor toContributor(JSONObject commit) {
        String name = (String) ((Map)((Map)commit.get("commit")).get("author")).get("name");
        String login = (String) ((Map)commit.get("author")).get("login");
        String profileUrl = (String) ((Map)commit.get("author")).get("html_url");
        return new DefaultContributor(name, login, profileUrl);
    }
}

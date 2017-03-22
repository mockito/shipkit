package org.mockito.release.notes.contributors;

import org.json.simple.JsonObject;
import org.mockito.release.notes.model.Contributor;

import java.util.Map;

/**
 * Provides means to parse JsonObjects returned from calling GitHub API.
 */
public class GitHubCommitsJSON {

    /**
     * Parses GitHub JsonObject in accordance to the API (https://developer.github.com/v3/repos/commits)
     */
    static Contributor toContributor(JsonObject commit) {
        try {
            String name = (String) ((Map) ((Map) commit.get("commit")).get("author")).get("name");
            String login = (String) ((Map) commit.get("author")).get("login");
            String profileUrl = (String) ((Map) commit.get("author")).get("html_url");
            return new DefaultContributor(name, login, profileUrl);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static boolean containsRevision(JsonObject commit, String revision) {
        String sha = (String)commit.get("sha");
        return sha.equals(revision);
    }

}

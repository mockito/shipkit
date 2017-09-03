package org.shipkit.internal.notes.contributors.github;

import org.json.simple.JsonObject;
import org.shipkit.internal.notes.contributors.DefaultProjectContributor;
import org.shipkit.internal.notes.model.ProjectContributor;

/**
 * Provides means to parse JsonObjects returned from calling GitHub API.
 */
public class GitHubAllContributorsJson {

    /**
     * Parses GitHub JsonObject in accordance to the API
     * @param contributor Represent project contribution: https://developer.github.com/v3/repos/#list-contributors and
     * @param user Represent user: https://developer.github.com/v3/users/#get-a-single-user
     * @return Contributor object based on project contribution and user
     */
    public static ProjectContributor toContributor(JsonObject contributor, JsonObject user) {
        String name = user.getString("name");
        String login = user.getString("login");
        String profileUrl = contributor.getString("html_url");
        Integer numberOfContributors = contributor.getInteger("contributions");
        return new DefaultProjectContributor(textOrEmpty(name), login, profileUrl, numberOfContributors);
    }

    private static String textOrEmpty(String text) {
        return text != null ? text : "";
    }
}

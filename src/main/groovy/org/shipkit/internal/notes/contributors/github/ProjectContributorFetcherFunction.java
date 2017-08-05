package org.shipkit.internal.notes.contributors.github;

import org.json.simple.JsonObject;
import org.shipkit.internal.notes.model.ProjectContributor;
import org.shipkit.internal.notes.util.Function;
import org.shipkit.internal.notes.util.GitHubObjectFetcher;

/**
 * Extracts the url form a given {@link JsonObject} and fetches the {@link ProjectContributor} info using the extracted
 * url.
 */
class ProjectContributorFetcherFunction implements Function<JsonObject, ProjectContributor> {

    private final GitHubObjectFetcher objectFetcher;

    public ProjectContributorFetcherFunction(GitHubObjectFetcher objectFetcher) {
        this.objectFetcher = objectFetcher;
    }

    @Override
    public ProjectContributor apply(JsonObject contributor) {
        String url = (String) contributor.get("url");
        JsonObject user;
        try {
            user = objectFetcher.getPage(url);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while fetching contributor using " + url + "!", e);
        }

        return GitHubAllContributorsJson.toContributor(contributor, user);
    }
}

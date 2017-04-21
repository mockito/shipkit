package org.mockito.release.notes.contributors;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.mockito.release.notes.model.Contributor;
import org.mockito.release.notes.util.GitHubListFetcher;
import org.mockito.release.notes.util.GitHubObjectFetcher;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GitHubAllContributorsFetcher {

    private static final Logger LOG = Logging.getLogger(GitHubAllContributorsFetcher.class);

    ContributorsSet fetchAllContributorsForProject(String repository, String authToken) {
        LOG.lifecycle("Querying GitHub API for all contributors for project");
        ContributorsSet result = new DefaultContributorsSet();

        try {
            GitHubProjectContributors contributors =
                    GitHubProjectContributors.authenticatingWith(repository, authToken).build();

            while(contributors.hasNextPage()) {
                List<JsonObject> page = contributors.nextPage();
                result.addAllContributors(extractContributors(page, authToken));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Set<Contributor> extractContributors(List<JsonObject> page, String authToken) throws IOException, DeserializationException {
        Set<Contributor> result = new HashSet<Contributor>();
        for (JsonObject contributor : page) {
            String url = (String) contributor.get("url");
            GitHubObjectFetcher userFetcher = new GitHubObjectFetcher(url, authToken);
            JsonObject user = userFetcher.getPage();
            result.add(GitHubAllContributorsJson.toContributor(contributor, user));
        }
        return result;
    }

    private static class GitHubProjectContributors {
        private final GitHubListFetcher fetcher;
        private List<JsonObject> lastFetchedPage;

        static GitHubProjectContributorsBuilder authenticatingWith(String repository, String authToken) {
            return new GitHubProjectContributorsBuilder(repository, authToken);
        }

        private GitHubProjectContributors(String nextPageUrl) {
            fetcher = new GitHubListFetcher(nextPageUrl);
        }

        public boolean hasNextPage() {
            return fetcher.hasNextPage();
        }

        public List<JsonObject> nextPage() throws IOException, DeserializationException {
            lastFetchedPage = fetcher.nextPage();
            return lastFetchedPage;
        }
    }

    private static class GitHubProjectContributorsBuilder {

        private final String repository;
        private final String authToken;

        public GitHubProjectContributorsBuilder(String repository, String authToken) {
            this.repository = repository;
            this.authToken = authToken;
        }

        GitHubProjectContributors build() {
            // see API doc: https://developer.github.com/v3/repos/#list-contributors
            String nextPageUrl = String.format("%s%s",
                    "https://api.github.com/repos/" + repository + "/contributors",
                    "?access_token=" + authToken);
            return new GitHubProjectContributors(nextPageUrl);
        }
    }
}

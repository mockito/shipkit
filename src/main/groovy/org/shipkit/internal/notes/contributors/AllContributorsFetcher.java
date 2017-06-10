package org.shipkit.internal.notes.contributors;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.shipkit.internal.notes.model.ProjectContributor;
import org.shipkit.internal.notes.util.Function;
import org.shipkit.internal.notes.util.GitHubListFetcher;
import org.shipkit.internal.notes.util.GitHubObjectFetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Gets all contributors from the repository
 * https://developer.github.com/v3/repos/#list-contributors
 */
class AllContributorsFetcher {

    private static final Logger LOG = Logging.getLogger(AllContributorsFetcher.class);

    ProjectContributorsSet fetchAllContributorsForProject(String apiUrl, String repository, String readOnlyAuthToken) {
        LOG.lifecycle("  Querying GitHub API for all contributors for project");
        ProjectContributorsSet result = new DefaultProjectContributorsSet();

        try {
            GitHubProjectContributors contributors =
                    GitHubProjectContributors.authenticatingWith(apiUrl, repository, readOnlyAuthToken).build();

            while(contributors.hasNextPage()) {
                List<JsonObject> page = contributors.nextPage();
                result.addAllContributors(extractContributors(page, readOnlyAuthToken));
            }
        } catch (Exception e) {
            throw new RuntimeException("Problems fetching and parsing contributors from GitHub repo: '" + repository
                    + "', using read only token: 'readOnlyAuthToken'", e);
        }
        return result;
    }

    private Set<ProjectContributor> extractContributors(List<JsonObject> page, final String readOnlyAuthToken) throws IOException, DeserializationException {
        Set<ProjectContributor> result = new HashSet<ProjectContributor>();
        //Since returned contributor does not have 'name' element, we need to fetch the user data to get his name
        //TODO add static caching of this. Names don't change that often, let's just cache this forever in build cache.
        ExecutorService executor = Executors.newFixedThreadPool(4);
        int maxSizePerCallable = 25;

        List<Future<Set<ProjectContributor>>> futures = new ArrayList<Future<Set<ProjectContributor>>>();
        if (page.size() > 0) {
            for (int i = 0; i < Math.max(page.size() / maxSizePerCallable, 1); i++) {
                List<JsonObject> subList = page.subList(i * maxSizePerCallable, Math.min((i + 1) * maxSizePerCallable, page.size()));

                GitHubObjectFetcher objectFetcher = new GitHubObjectFetcher(readOnlyAuthToken);
                Function<JsonObject, ProjectContributor> projectContributorFetcherFunction = new ProjectContributorFetcherFunction(objectFetcher);
                Callable<Set<ProjectContributor>> callable = new FetcherCallable<JsonObject, ProjectContributor>(subList, projectContributorFetcherFunction);

                futures.add(executor.submit(callable));
            }
        }

        for (Future<Set<ProjectContributor>> future: futures) {
            try {
                result.addAll(future.get());
            } catch (InterruptedException e) {
                throw new IOException(e);
            } catch (ExecutionException e) {
                throw new IOException(e.getCause());
            }
        }

        return result;
    }

    private static class GitHubProjectContributors {
        private final GitHubListFetcher fetcher;
        private List<JsonObject> lastFetchedPage;

        static GitHubProjectContributorsBuilder authenticatingWith(String apiUrl, String repository, String readOnlyAuthToken) {
            return new GitHubProjectContributorsBuilder(apiUrl, repository, readOnlyAuthToken);
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

        private final String apiUrl;
        private final String repository;
        private final String readOnlyAuthToken;

        public GitHubProjectContributorsBuilder(String apiUrl, String repository, String readOnlyAuthToken) {
            this.apiUrl = apiUrl;
            this.repository = repository;
            this.readOnlyAuthToken = readOnlyAuthToken;
        }

        GitHubProjectContributors build() {
            // see API doc: https://developer.github.com/v3/repos/#list-contributors
            String nextPageUrl = apiUrl + "/repos/" + repository + "/contributors" +
                    "?access_token=" + readOnlyAuthToken +
                    "&per_page=100";
            return new GitHubProjectContributors(nextPageUrl);
        }
    }
}

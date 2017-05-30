package org.shipkit.notes.contributors;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.shipkit.notes.model.Contributor;
import org.shipkit.notes.util.GitHubListFetcher;

import java.io.IOException;
import java.util.*;

import static org.shipkit.internal.util.ArgumentValidation.notNull;
import static org.shipkit.internal.util.DateUtil.forGitHub;
import static org.shipkit.internal.util.DateUtil.yesterday;

/**
 * Fetches recent contributors from GitHub using the "commit" end point.
 * Uses https://developer.github.com/v3/repos/commits/
 * We use it because the "contributors" endpoint does not return the most recent contributors as documented
 * (see {@link AllContributorsFetcher}).
 */
class RecentContributorsFetcher {

    private static final Logger LOG = Logging.getLogger(RecentContributorsFetcher.class);

    /**
     * Contributors that pushed commits to the repo withing the last 24hrs
     */
    public Collection<Contributor> fetchContributorsSinceYesterday(String repository, String readOnlyAuthToken) {
        return fetchContributors(repository, readOnlyAuthToken, yesterday(), null);
    }

    /**
     * Contributors that pushed commits to the repo within the time span.
     * @param dateSince - must not be null, the since date
     * @param dateUntil - can be null, it means there is no end date
     */
    public Collection<Contributor> fetchContributors(String repository, String readOnlyAuthToken, Date dateSince, Date dateUntil) {
        LOG.info("Querying GitHub API for commits (for contributors)");
        Set<Contributor> contributors = new LinkedHashSet<Contributor>();

        try {
            GitHubCommits commits = GitHubCommits
                    .with(repository, readOnlyAuthToken, dateSince, dateUntil)
                    .build();

            while(commits.hasNextPage()) {
                List<JsonObject> page = commits.nextPage();
                contributors.addAll(extractContributors(page));
            }
        } catch (Exception e) {
            throw new RuntimeException("Problems fetching commits from GitHub", e);
        }

        return contributors;
    }

    private Set<Contributor> extractContributors(List<JsonObject> commits) {
        Set<Contributor> result = new HashSet<Contributor>();
        for (JsonObject commit : commits) {
            Contributor contributor = GitHubCommitsJSON.toContributor(commit);
            if(contributor != null) {
                result.add(contributor);
            }
        }
        return result;
    }

    private static class GitHubCommits {

        private final GitHubListFetcher fetcher;
        private List<JsonObject> lastFetchedPage;

        private GitHubCommits(String nextPageUrl) {
            fetcher = new GitHubListFetcher(nextPageUrl);
        }

        boolean hasNextPage() {
            return fetcher.hasNextPage();
        }

        List<JsonObject> nextPage() throws IOException, DeserializationException {
            lastFetchedPage = fetcher.nextPage();
            return lastFetchedPage;
        }

        static GitHubCommitsBuilder with(String repository, String readOnlyAuthToken, Date dateSince, Date dateUntil) {
            return new GitHubCommitsBuilder(repository, readOnlyAuthToken, dateSince, dateUntil);
        }

        private static class GitHubCommitsBuilder {
            private final String repository;
            private final String readOnlyAuthToken;
            private final Date dateSince;
            private final Date dateUntil;

            private GitHubCommitsBuilder(String repository, String readOnlyAuthToken, Date dateSince, Date dateUntil) {
                notNull(repository, "repository", readOnlyAuthToken, "readOnlyAuthToken", dateSince, "dateSince");
                this.repository = repository;
                this.readOnlyAuthToken = readOnlyAuthToken;
                this.dateSince = dateSince;
                this.dateUntil = dateUntil;
            }

            GitHubCommits build() {
                // see API doc: https://developer.github.com/v3/repos/commits/#list-commits-on-a-repository
                String nextPageUrl = "https://api.github.com/repos/" + repository + "/commits"
                        + "?access_token=" + readOnlyAuthToken
                        + "&since=" + forGitHub(dateSince)
                        + ((dateUntil != null)? "&until=" + forGitHub(dateUntil) : "")
                        + "&page=1&per_page=100";
                return new GitHubCommits(nextPageUrl);
            }
        }
    }
}

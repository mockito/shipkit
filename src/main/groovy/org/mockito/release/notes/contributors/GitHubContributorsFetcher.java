package org.mockito.release.notes.contributors;

import org.json.simple.JSONObject;
import org.mockito.release.notes.model.Contribution;
import org.mockito.release.notes.model.Contributor;
import org.mockito.release.notes.util.GitHubFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GitHubContributorsFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(GitHubContributorsFetcher.class);

    ContributorsMap fetchContributors(String authToken, Collection<Contribution> contributions, String fromRevision, String toRevision) {
        ContributorsMap result = new DefaultContributorsMap();
        LOG.info("Querying GitHub API for commits (for contributors)");

        Set<String> authors = getAuthors(contributions);

        try {
            GitHubCommits commits = GitHubCommits.authenticatingWith(authToken)
                    .fromRevision(fromRevision)
                    .toRevision(toRevision)
                    .build();

            while(!authors.isEmpty() && commits.hasNextPage()) {
                List<JSONObject> page = commits.nextPage();
                result.putAll(extractAuthors(page, authors));
            }
        } catch (Exception e) {
            throw new RuntimeException("Problems fetching commits from GitHub", e);
        }

        return result;
    }

    private Set<String> getAuthors(Collection<Contribution> contributions) {
        Set<String> authors = new HashSet<String>();
        for (Contribution contribution : contributions) {
            authors.add(contribution.getAuthorName());
        }
        return authors;
    }

    private ContributorsMap extractAuthors(List<JSONObject> commits, Set<String> authors) {
        ContributorsMap map = new DefaultContributorsMap();
        for (JSONObject commit : commits) {
            Contributor contributor = GitHubCommitsJSON.toContributor(commit);
            if(authors.contains(contributor.getName())) {
                map.put(contributor.getName(), contributor);
                authors.remove(contributor.getName());
            }
            if(authors.isEmpty()) {
                return map;
            }
        }
        return map;
    }

    private static class GitHubCommits extends GitHubFetcher {

        private final String fromRevision;

        private GitHubCommits(String nextPageUrl, String fromRevision) {
            super(nextPageUrl);
            this.fromRevision = fromRevision;
        }

        static GitHubCommitsBuilder authenticatingWith(String authToken) {
            return new GitHubCommitsBuilder(authToken);
        }

        private static class GitHubCommitsBuilder {
            private final String authToken;
            private String fromRevision;
            private String toRevision;

            private GitHubCommitsBuilder(String authToken) {
                this.authToken = authToken;
            }

            GitHubCommitsBuilder fromRevision(String fromRevision) {
                this.fromRevision = fromRevision;
                return this;
            }

            GitHubCommitsBuilder toRevision(String toRevision) {
                this.toRevision = toRevision;
                return this;
            }

            GitHubCommits build() {
                // see API doc: https://developer.github.com/v3/repos/commits/#list-commits-on-a-repository
                String nextPageUrl = String.format("%s%s%s",
                        "https://api.github.com/repos/mockito/mockito/commits?access_token=" + authToken,
                        max(toRevision),
                        "&page=1");
                return new GitHubCommits(nextPageUrl, fromRevision);
            }

            private String max(String toRevision) {
                if(toRevision != null && !toRevision.isEmpty()) {
                    return "&max=" + toRevision;
                }
                return "";
            }
        }
    }
}

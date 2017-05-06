package org.mockito.release.notes.contributors;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.mockito.release.internal.gradle.util.StringUtil;
import org.mockito.release.notes.model.Contributor;
import org.mockito.release.notes.util.GitHubListFetcher;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GitHubLastContributorsFetcher {

    private static final Logger LOG = Logging.getLogger(GitHubLastContributorsFetcher.class);

    ContributorsSet fetchContributors(String repository, String readOnlyAuthToken, Collection<String> authorNames, String fromRevision, String toRevision) {
        LOG.info("Querying GitHub API for commits (for contributors)");
        ContributorsSet result = new DefaultContributorsSet();

        Set<String> remaining = new HashSet<String>(authorNames);

        try {
            GitHubCommits commits = GitHubCommits.authenticatingWith(repository, readOnlyAuthToken)
                    .fromRevision(fromRevision)
                    .toRevision(toRevision)
                    .build();

            while(!remaining.isEmpty() && commits.hasNextPage()) {
                List<JsonObject> page = commits.nextPage();
                result.addAllContributors(extractContributors(page, remaining));
            }
            if (!remaining.isEmpty()) {
                LOG.lifecycle("{} contributor(s) will not have link to GitHub profile in release notes data.\n" +
                                "  We were not able to extract contributor information from GitHub API using 'commits' endpoint.\n" +
                                "  Contributors with no profile: \n  {}",
                        remaining.size(), StringUtil.join(remaining, ", "));
            }
        } catch (Exception e) {
            LOG.info("Problems fetching commits from GitHub", e);
            throw new RuntimeException("Problems fetching commits from GitHub", e);
        }

        return result;
    }

    private Set<Contributor> extractContributors(List<JsonObject> commits, Set<String> authors) {
        Set<Contributor> result = new HashSet<Contributor>();
        for (JsonObject commit : commits) {
            Contributor contributor = GitHubCommitsJSON.toContributor(commit);
            if(contributor != null) {
                if (authors.contains(contributor.getName())) {
                    result.add(contributor);
                    authors.remove(contributor.getName());
                }
                if (authors.isEmpty()) {
                    return result;
                }
            }
        }
        return result;
    }

    private static class GitHubCommits {

        private final String fromRevision;
        private final GitHubListFetcher fetcher;
        private List<JsonObject> lastFetchedPage;

        private GitHubCommits(String nextPageUrl, String fromRevision) {
            fetcher = new GitHubListFetcher(nextPageUrl);
            this.fromRevision = fromRevision;
        }

        boolean hasNextPage() {
            return !containsRevision(lastFetchedPage, fromRevision) && fetcher.hasNextPage();
        }

        private boolean containsRevision(List<JsonObject> lastFetchedPage, String revision) {
            if(lastFetchedPage == null) {
                return false;
            }
            for (JsonObject commit : lastFetchedPage) {
                if(GitHubCommitsJSON.containsRevision(commit, revision)) {
                    return true;
                }
            }
            return false;
        }

        List<JsonObject> nextPage() throws IOException, DeserializationException {
            lastFetchedPage = fetcher.nextPage();
            return lastFetchedPage;
        }

        static GitHubCommitsBuilder authenticatingWith(String repository, String readOnlyAuthToken) {
            return new GitHubCommitsBuilder(repository, readOnlyAuthToken);
        }

        private static class GitHubCommitsBuilder {
            private final String repository;
            private final String readOnlyAuthToken;
            private String fromRevision;
            private String toRevision;

            private GitHubCommitsBuilder(String repository, String readOnlyAuthToken) {
                this.repository = repository;
                this.readOnlyAuthToken = readOnlyAuthToken;
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
                String nextPageUrl = String.format("%s%s%s%s",
                        "https://api.github.com/repos/" + repository + "/commits",
                        "?access_token=" + readOnlyAuthToken,
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

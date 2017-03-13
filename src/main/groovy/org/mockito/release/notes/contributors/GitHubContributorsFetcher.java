package org.mockito.release.notes.contributors;

import org.json.simple.JSONObject;
import org.mockito.release.notes.model.Commit;
import org.mockito.release.notes.model.Contribution;
import org.mockito.release.notes.model.Contributor;
import org.mockito.release.notes.util.GitHubFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class GitHubContributorsFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(GitHubContributorsFetcher.class);

    ContributorsSet fetchContributors(String authToken, Collection<Contribution> contributions, String fromRevision, String toRevision) {
        ContributorsSet result = new DefaultContributorsSet();
        LOG.info("Querying GitHub API for commits (for contributors)");

        Set<String> authors = getAuthors(contributions);

        try {
            GitHubCommits commits = GitHubCommits.authenticatingWith(authToken)
                    .fromRevision(fromRevision)
                    .toRevision(toRevision)
                    .build();

            while(!authors.isEmpty() && commits.hasNextPage()) {
                List<JSONObject> page = commits.nextPage();
                result.addAllContributors(extractContributors(page, authors));
            }
            if(!authors.isEmpty()) {
                for (String author : authors) {
                    //TODO SF Maybe use warn or error level here?
                    LOG.info(cantFindProfileForAuthorError(author, contributions));
                }
            }
        } catch (Exception e) {
            LOG.info("Problems fetching commits from GitHub", e);
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

    private Set<Contributor> extractContributors(List<JSONObject> commits, Set<String> authors) {
        Set<Contributor> result = new HashSet<Contributor>();
        for (JSONObject commit : commits) {
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

    private String cantFindProfileForAuthorError(String author, Collection<Contribution> contributions) {
        Collection<Commit> commits = getCommitsForAuthors(contributions, author);
        return "Can't find Profile for " + author + " commits: " + format(commits);
    }

    private String format(Collection<Commit> commits) {
        Set<String> ids = new HashSet<String>();
        for (Commit commit : commits) {
            ids.add(commit.getCommitId());
        }
        return join(", ", ids);
    }

    private String join(String delimiter, Set<String> ids) {
        String text = "";
        for (String id : ids) {
            text = text + id + delimiter;
        }
        return removeLastDelimiterIfExist(delimiter, text);
    }

    private String removeLastDelimiterIfExist(String delimiter, String text) {
        if(text.length() > delimiter.length()) {
            text = text.substring(0, text.length() - delimiter.length());
        }
        return text;
    }

    private Collection<Commit> getCommitsForAuthors(Collection<Contribution> contributions, String author) {
        Set<Commit> commits = new HashSet<Commit>();
        for (Contribution contribution : contributions) {
            if(contribution.getAuthorName().equals(author)) {
                commits.addAll(contribution.getCommits());
            }
        }
        return commits;
    }

    private static class GitHubCommits {

        private final String fromRevision;
        private final GitHubFetcher fetcher;
        private List<JSONObject> lastFetchedPage;

        private GitHubCommits(String nextPageUrl, String fromRevision) {
            fetcher = new GitHubFetcher(nextPageUrl);
            this.fromRevision = fromRevision;
        }

        boolean hasNextPage() {
            return !containsRevision(lastFetchedPage, fromRevision) && fetcher.hasNextPage();
        }

        private boolean containsRevision(List<JSONObject> lastFetchedPage, String revision) {
            if(lastFetchedPage == null) {
                return false;
            }
            for (JSONObject commit : lastFetchedPage) {
                if(GitHubCommitsJSON.containsRevision(commit, revision)) {
                    return true;
                }
            }
            return false;
        }

        List<JSONObject> nextPage() throws IOException {
            lastFetchedPage = fetcher.nextPage();
            return lastFetchedPage;
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
                String nextPageUrl = String.format("%s%s%s%s",
                        "https://api.github.com/repos/mockito/mockito/commits",
                        "?access_token=" + authToken,
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

package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.ContributionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubContributorsProvider implements ContributorsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(GitHubContributorsProvider.class);
    private final String authToken;

    GitHubContributorsProvider(String authToken) {
        this.authToken = authToken;
    }

    public ContributorsSet mapContributorsToGitHubUser(ContributionSet contributions, String fromRevision, String toRevision) {
        LOG.info("Parsing {} commits with {} contributors", contributions.getAllCommits().size(), contributions.getAuthorCount());
        return new GitHubContributorsFetcher().fetchContributors(authToken, contributions.getContributions(), fromRevision, toRevision);
    }
}

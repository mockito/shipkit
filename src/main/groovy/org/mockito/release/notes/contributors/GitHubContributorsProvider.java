package org.mockito.release.notes.contributors;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.mockito.release.notes.model.ContributionSet;

public class GitHubContributorsProvider implements ContributorsProvider {

    private static final Logger LOG = Logging.getLogger(GitHubContributorsProvider.class);

    private final String repository;
    private final String readOnlyAuthToken;

    GitHubContributorsProvider(String repository, String readOnlyAuthToken) {
        this.repository = repository;
        this.readOnlyAuthToken = readOnlyAuthToken;
    }

    @Override
    public ContributorsSet mapContributorsToGitHubUser(ContributionSet contributions, String fromRevision, String toRevision) {
        LOG.info("Parsing {} commits with {} contributors", contributions.getAllCommits().size(), contributions.getAuthorCount());
        return new GitHubLastContributorsFetcher().fetchContributors(repository, readOnlyAuthToken, contributions.getContributions(), fromRevision, toRevision);
    }

    @Override
    public ProjectContributorsSet getAllContributorsForProject() {
        return new GitHubAllContributorsFetcher().fetchAllContributorsForProject(repository, readOnlyAuthToken);
    }
}

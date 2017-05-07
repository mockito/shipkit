package org.mockito.release.notes.contributors;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.util.Collection;

public class GitHubContributorsProvider implements ContributorsProvider {

    private static final Logger LOG = Logging.getLogger(GitHubContributorsProvider.class);

    private final String repository;
    private final String readOnlyAuthToken;

    GitHubContributorsProvider(String repository, String readOnlyAuthToken) {
        this.repository = repository;
        this.readOnlyAuthToken = readOnlyAuthToken;
    }

    @Override
    public ContributorsSet mapContributorsToGitHubUser(Collection<String> authorNames, String fromRevision, String toRevision) {
        LOG.info("Fetching contributor information for {} authors", authorNames.size());
        return new GitHubLastContributorsFetcher().fetchContributors(repository, readOnlyAuthToken, authorNames, fromRevision, toRevision);
    }

    @Override
    public ProjectContributorsSet getAllContributorsForProject() {
        return new GitHubAllContributorsFetcher().fetchAllContributorsForProject(repository, readOnlyAuthToken);
    }
}

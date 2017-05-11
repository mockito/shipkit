package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.Contributor;

import java.util.Collection;

public class GitHubContributorsProvider implements ContributorsProvider {

    private final String repository;
    private final String readOnlyAuthToken;

    GitHubContributorsProvider(String repository, String readOnlyAuthToken) {
        this.repository = repository;
        this.readOnlyAuthToken = readOnlyAuthToken;
    }

    @Override
    public ContributorsSet mapContributorsToGitHubUser(Collection<String> authorNames, String fromRevision, String toRevision) {
        throw new RuntimeException("Not supported any more! TODO: remove");
    }

    @Override
    public ProjectContributorsSet getAllContributorsForProject() {
        ProjectContributorsSet contributors = new AllContributorsFetcher().fetchAllContributorsForProject(repository, readOnlyAuthToken);
        Collection<Contributor> recent = new RecentContributorsFetcher().fetchContributorsSinceYesterday(repository, readOnlyAuthToken);
        return mergeContributors(contributors, recent);
    }

    static ProjectContributorsSet mergeContributors(ProjectContributorsSet contributors, Collection<Contributor> recent) {
        for (Contributor c : recent) {
            //Create project contributor with single contribution
            DefaultProjectContributor newContributor = new DefaultProjectContributor(c);
            //The implementation of ProjectContributorsSet ensures that we don't have duplicates
            // and that we don't overwrite existing contributors
            contributors.addContributor(newContributor);
        }

        return contributors;
    }
}

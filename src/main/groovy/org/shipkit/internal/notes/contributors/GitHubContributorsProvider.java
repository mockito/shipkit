package org.shipkit.internal.notes.contributors;

import org.shipkit.internal.notes.model.Contributor;

import java.util.Collection;

public class GitHubContributorsProvider implements ContributorsProvider {

    private final String apiUrl;
    private final String repository;
    private final String readOnlyAuthToken;

    GitHubContributorsProvider(String apiUrl, String repository, String readOnlyAuthToken) {
        this.apiUrl = apiUrl;
        this.repository = repository;
        this.readOnlyAuthToken = readOnlyAuthToken;
    }

    @Override
    public ProjectContributorsSet getAllContributorsForProject() {
        ProjectContributorsSet contributors = new ContributorsFetcher().fetchContributorsForProject(apiUrl, repository, readOnlyAuthToken);
        Collection<Contributor> recent = new RecentContributorsFetcher().fetchContributorsSinceYesterday(apiUrl, repository, readOnlyAuthToken);
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

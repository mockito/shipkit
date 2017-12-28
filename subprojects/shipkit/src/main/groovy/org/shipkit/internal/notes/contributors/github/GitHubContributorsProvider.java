package org.shipkit.internal.notes.contributors.github;

import org.shipkit.internal.notes.contributors.ContributorsProvider;
import org.shipkit.internal.notes.contributors.DefaultProjectContributor;
import org.shipkit.internal.notes.contributors.ProjectContributorsSet;
import org.shipkit.internal.notes.model.Contributor;

import java.util.Collection;

public class GitHubContributorsProvider implements ContributorsProvider {

    private final String apiUrl;
    private final String repository;
    private final String readOnlyAuthToken;
    private final Collection<String> ignoredContributors;

    GitHubContributorsProvider(String apiUrl, String repository, String readOnlyAuthToken, Collection<String> ignoredContributors) {
        this.apiUrl = apiUrl;
        this.repository = repository;
        this.readOnlyAuthToken = readOnlyAuthToken;
        this.ignoredContributors = ignoredContributors;
    }

    @Override
    public ProjectContributorsSet getAllContributorsForProject() {
        ProjectContributorsSet contributors = new GitHubContributorsFetcher(ignoredContributors).fetchContributorsForProject(apiUrl, repository, readOnlyAuthToken);
        Collection<Contributor> recent = new RecentContributorsFetcher().fetchContributorsSinceYesterday(apiUrl, repository, readOnlyAuthToken);
        return mergeContributors(contributors, recent);
    }

    static ProjectContributorsSet mergeContributors(ProjectContributorsSet contributors, Collection<Contributor>
        recent) {
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

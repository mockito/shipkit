package org.shipkit.internal.gradle.contributors.github;


import org.shipkit.internal.gradle.contributors.AllContributorsFetcherTask;
import org.shipkit.internal.notes.contributors.ContributorsProvider;
import org.shipkit.internal.notes.contributors.github.Contributors;

public class GithubContributorsFetcherTask extends AllContributorsFetcherTask {

    @Override
    protected ContributorsProvider getContributorsProvider() {
        return Contributors.getGitHubContributorsProvider(getApiUrl(), getRepository(), getReadOnlyAuthToken());
    }
}

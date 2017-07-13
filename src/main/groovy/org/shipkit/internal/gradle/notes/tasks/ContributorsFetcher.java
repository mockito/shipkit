package org.shipkit.internal.gradle.notes.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.notes.ContributorsFetcherTask;
import org.shipkit.internal.notes.contributors.AllContributorsSerializer;
import org.shipkit.internal.notes.contributors.Contributors;
import org.shipkit.internal.notes.contributors.GitHubContributorsProvider;
import org.shipkit.internal.notes.contributors.ProjectContributorsSet;
import org.shipkit.internal.notes.util.IOUtil;

public class ContributorsFetcher {

    private static final Logger LOG = Logging.getLogger(ContributorsFetcherTask.class);

    public void fetchContributors(ContributorsFetcherTask task) {
        LOG.lifecycle("  Fetching all contributors for project");

        GitHubContributorsProvider contributorsProvider = Contributors.getGitHubContributorsProvider(
            task.getApiUrl(), task.getRepository(), task.getReadOnlyAuthToken());
        ProjectContributorsSet contributors = contributorsProvider.getAllContributorsForProject();

        AllContributorsSerializer serializer = new AllContributorsSerializer();
        final String json = serializer.serialize(contributors);
        IOUtil.writeFile(task.getOutputFile(), json);

        LOG.lifecycle("  Serialized contributors information: {}", task.getProject().relativePath(task.getOutputFile()));
    }
}

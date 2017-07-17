package org.shipkit.internal.gradle.notes.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.notes.FetchContributorsTask;
import org.shipkit.internal.notes.contributors.ProjectContributorsSerializer;
import org.shipkit.internal.notes.contributors.github.Contributors;
import org.shipkit.internal.notes.contributors.github.GitHubContributorsProvider;
import org.shipkit.internal.notes.contributors.ProjectContributorsSet;
import org.shipkit.internal.notes.util.IOUtil;

public class FetchContributors {

    private static final Logger LOG = Logging.getLogger(FetchContributorsTask.class);

    public void fetchContributors(final ContributorsProvider contributorsProvider, FetchContributorsTask task) {
        LOG.lifecycle("  Fetching all contributors for project");

        ProjectContributorsSet contributors = contributorsProvider.getAllContributorsForProject();

        ProjectContributorsSerializer serializer = new ProjectContributorsSerializer();
        final String json = serializer.serialize(contributors);
        IOUtil.writeFile(task.getOutputFile(), json);

        LOG.lifecycle("  Serialized contributors information: {}", task.getProject().relativePath(task.getOutputFile()));
    }
}

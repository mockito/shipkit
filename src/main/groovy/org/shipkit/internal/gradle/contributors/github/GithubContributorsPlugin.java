package org.shipkit.internal.gradle.contributors.github;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.internal.gradle.contributors.AllContributorsFetcherTask;
import org.shipkit.internal.gradle.contributors.ContributorsPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;

import static org.shipkit.internal.gradle.util.BuildConventions.contributorsFile;


public class GithubContributorsPlugin extends ContributorsPlugin {

    @Override
    protected void fetchAllTask(final Project project, final ReleaseConfiguration conf) {
        project.getTasks().create(FETCH_ALL_CONTRIBUTORS_TASK, GithubContributorsFetcherTask.class, new Action<AllContributorsFetcherTask>() {
            @Override
            public void execute(final AllContributorsFetcherTask task) {
                task.setGroup(TaskMaker.TASK_GROUP);
                task.setDescription("Fetch info about all project contributors from GitHub and store it in file");
                task.setOutputFile(contributorsFile(project));
                task.setApiUrl(conf.getGitHub().getApiUrl());
                task.setReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
                task.setRepository(conf.getGitHub().getRepository());
                task.setEnabled(conf.getTeam().getContributors().isEmpty());
            }
        });
    }
}

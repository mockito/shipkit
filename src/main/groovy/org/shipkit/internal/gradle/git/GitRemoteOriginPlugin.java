package org.shipkit.internal.gradle.git;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.notes.vcs.IdentifyGitOriginRepoTask;
import org.shipkit.internal.util.ResultHandler;

/**
 * Plugin that adds task for getting current git origin.
 * Since this information is acquired by calling an external process, it is not available
 * in configuration phase. Therefore you should use #provideOriginTo method for accessing it.
 *
 * Adds following tasks:
 * <ul>
 *     <li>
 *         'identifyGitOrigin' - Identifies current git origin repo.
*      </li>
 * </ul>
 */
public class GitRemoteOriginPlugin implements Plugin<Project> {

    private static final String IDENTIFY_GIT_ORIGIN_TASK = "identifyGitOrigin";

    @Override
    public void apply(Project project) {
        TaskMaker.task(project, IDENTIFY_GIT_ORIGIN_TASK, IdentifyGitOriginRepoTask.class, new Action<IdentifyGitOriginRepoTask>() {
            public void execute(IdentifyGitOriginRepoTask t) {
                t.setDescription("Identifies current git origin repo.");
            }
        });
    }

    public static class GitOriginAuth{
        private final String originRepositoryUrl;
        private final String originRepositoryName;

        public GitOriginAuth(String originRepositoryUrl, String originRepositoryName) {
            this.originRepositoryUrl = originRepositoryUrl;
            this.originRepositoryName = originRepositoryName;
        }

        /**
         * URL of the GitHub repository along with authentication data if it was specified.
         * Repository is based on local git origin.
         * It can be in one of the following formats:
         * - https://github.com/{repo}.git
         * - https://{ghUser}:{ghWriteToken}@github.com/{repo}.git
         */
        public String getOriginRepositoryUrl() {
            return originRepositoryUrl;
        }

        /**
         * Name of the GitHub repository in format USER/REPO, eg. "mockito/shipkit"
         */
        public String getOriginRepositoryName() {
            return originRepositoryName;
        }
    }

    /**
     * Configures some task that needs git origin information
     * and an action that is executed when the git origin is available.
     * This information is not available in configuration phase.
     *
     * @param needsOrigin some task that needs git origin information. Necessary 'dependsOn' will be added.
     * @param resultHandler executed when git origin info is ready. Hooked up as 'doLast' action.
     */
    public void provideOriginTo(final Task needsOrigin, final ResultHandler<GitOriginAuth> resultHandler) {
        final IdentifyGitOriginRepoTask originTask = (IdentifyGitOriginRepoTask) needsOrigin.getProject().getTasks().getByName(IDENTIFY_GIT_ORIGIN_TASK);
        needsOrigin.dependsOn(originTask);
        originTask.doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                chooseHandlerForOriginResult(originTask, resultHandler);
            }
        });
    }

    static void chooseHandlerForOriginResult(IdentifyGitOriginRepoTask originTask, ResultHandler<GitOriginAuth> resultHandler){
        if(originTask.getExecutionException() != null){
            resultHandler.onFailure(originTask.getExecutionException());
        } else {
            ShipkitConfiguration conf = originTask.getProject().getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();
            String originUrl = GitAuthPlugin.getGitHubUrl(
                conf.getGitHub().getWriteAuthUser(),
                originTask.getOriginRepo(),
                conf.getGitHub().getWriteAuthToken()
            );

            resultHandler.onSuccess(new GitOriginAuth(originUrl, originTask.getOriginRepo()));
        }
    }
}

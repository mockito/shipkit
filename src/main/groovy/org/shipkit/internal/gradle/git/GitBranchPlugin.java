package org.shipkit.internal.gradle.git;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.git.IdentifyGitBranchTask;
import org.shipkit.internal.gradle.util.TaskMaker;

/**
 * Adds tasks:
 * <ul>
 *     <li>identifyGitBranch - {@link IdentifyGitBranchTask}</li>
 * </ul>
 */
public class GitBranchPlugin implements Plugin<Project> {

    public static final String IDENTIFY_GIT_BRANCH = "identifyGitBranch";

    @Override
    public void apply(Project project) {
        TaskMaker.task(project, IDENTIFY_GIT_BRANCH, IdentifyGitBranchTask.class, new Action<IdentifyGitBranchTask>() {
            public void execute(final IdentifyGitBranchTask t) {
                t.setDescription("Identifies current git branch.");
            }
        });
    }

    /**
     * Configures some task that needs branch information
     * and an action that is executed when the branch info is available.
     *
     * @param needsBranch some task that needs branch information. Necessary 'dependsOn' will be added.
     * @param branchAction executed when branch info is ready. Hooked up as 'doLast' action.
     */
    public void provideBranchTo(Task needsBranch, final Action<String> branchAction) {
        final IdentifyGitBranchTask branchTask = (IdentifyGitBranchTask) needsBranch.getProject().getTasks().getByName(GitBranchPlugin.IDENTIFY_GIT_BRANCH);
        needsBranch.dependsOn(branchTask);
        branchTask.doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                branchAction.execute(branchTask.getBranch());
            }
        });
    }
}

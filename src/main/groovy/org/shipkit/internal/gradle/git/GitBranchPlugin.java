package org.shipkit.internal.gradle.git;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
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
}

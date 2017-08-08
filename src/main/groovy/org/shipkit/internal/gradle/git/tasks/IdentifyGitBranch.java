package org.shipkit.internal.gradle.git.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.git.IdentifyGitBranchTask;
import org.shipkit.internal.exec.Exec;

public class IdentifyGitBranch {

    private final Logger LOG = Logging.getLogger(IdentifyGitBranchTask.class);

    public void identifyBranch(IdentifyGitBranchTask task, String branch) {
        if (branch == null) {
            task.setBranch(Exec.getProcessRunner(task.getWorkDir())
                .run(task.getCommandLine())
                .trim());
        }
        LOG.lifecycle("  Current branch: " + task.getBranch());
    }

    public String getBranch(String branch) {
        if (branch == null) {
            throw new IdentifyGitBranchTask.BranchNotAvailableException("Don't know the branch yet because the task was not executed yet!");
        }
        return branch;
    }
}

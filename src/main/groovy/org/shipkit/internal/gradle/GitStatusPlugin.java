package org.shipkit.internal.gradle;


import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.exec.Exec;
import org.shipkit.exec.ProcessRunner;

/**
 * Adds extension {@link GitStatus} to the root project.
 * The extension contains information about the working copy.
 */
public class GitStatusPlugin implements Plugin<Project> {

    private GitStatus gitStatus;
    private final static Logger LOG = Logging.getLogger(Logger.class);

    @Override
    public void apply(Project project) {
        if (project.getParent() == null) {
            gitStatus = project.getExtensions().create(GitPlugin.class.getName(), GitStatus.class, project);
        } else {
            gitStatus = project.getRootProject().getPlugins().apply(GitStatusPlugin.class).getGitStatus();
        }
    }

    public GitStatus getGitStatus() {
        return gitStatus;
    }

    /**
     * The Git status of the working copy
     */
    public static class GitStatus {

        private final ProcessRunner runner;
        private volatile String branchName;
        private static final Object SYNC = new Object();

        public GitStatus(Project project) {
             this(Exec.getProcessRunner(project.getRootDir()));
        }

        public GitStatus(ProcessRunner runner) {
            this.runner = runner;
        }

        /**
         * Current git branch of the working copy
         */
        public String getBranch() {
            if (branchName == null || branchName.isEmpty()) {
                synchronized (SYNC) {
                    if (branchName == null || branchName.isEmpty()) {
                        branchName = runner.run("git", "rev-parse", "--abbrev-ref", "HEAD").trim();
                        LOG.lifecycle("  Identified current git branch as: " + branchName);
                    }
                }
            }
            return branchName;
        }
    }
}

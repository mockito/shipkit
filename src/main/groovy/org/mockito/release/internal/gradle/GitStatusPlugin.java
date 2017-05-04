package org.mockito.release.internal.gradle;


import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.mockito.release.exec.Exec;
import org.mockito.release.exec.ProcessRunner;

/**
 * Adds extension for configuring the {@link GitStatus} to the root project.
 */
public class GitStatusPlugin implements Plugin<Project> {

    private GitStatus gitStatus;

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

    public static class GitStatus {

        private final ProcessRunner processRunner;
        private volatile String branchName;
        private static final Object SYNC = new Object();

        public GitStatus(Project project) {
             processRunner = Exec.getProcessRunner(project.getRootDir());
        }

        public String getBranch() {
            if (branchName == null || branchName.isEmpty()) {
                synchronized (SYNC) {
                    if (branchName == null || branchName.isEmpty()) {
                        branchName = processRunner.run("git", "rev-parse", "--abbrev-ref", "HEAD").trim();
                    }
                }
            }
            return branchName;
        }
    }
}

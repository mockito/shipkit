package org.shipkit.internal.notes.vcs;

import org.shipkit.internal.exec.ProcessRunner;

public class GitRevisionProvider implements RevisionProvider {

    private final ProcessRunner runner;

    public GitRevisionProvider(ProcessRunner runner) {
        this.runner = runner;
    }

    @Override
    public String getRevisionForTagOrRevision(String tagOrRevision) {
        return runner.run("git", "rev-list", "-n", "1", tagOrRevision).trim();
    }
}

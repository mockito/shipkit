package org.shipkit.internal.notes.vcs;

import org.shipkit.internal.exec.ProcessRunner;

class GitLogProvider {

    private final ProcessRunner runner;

    GitLogProvider(ProcessRunner runner) {
        this.runner = runner;
    }

    public String getLog(String fromRev, String toRev, String format) {
        if(fromRev != null) {
            runner.run("git", "fetch", "origin", "+refs/tags/" + fromRev + ":refs/tags/" + fromRev);
            return runner.run("git", "log", format, fromRev + ".." + toRev);
        } else{
            runner.run("git", "fetch", "origin", toRev);
            return runner.run("git", "log", format, toRev);
        }
    }
}

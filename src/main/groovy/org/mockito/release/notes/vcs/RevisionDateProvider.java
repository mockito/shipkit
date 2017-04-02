package org.mockito.release.notes.vcs;

import org.mockito.release.exec.ProcessRunner;

import java.util.Date;

import static org.mockito.release.notes.internal.DateFormat.parseDate;

/**
 * Provides date of given vcs revision
 */
class RevisionDateProvider {

    private final ProcessRunner runner;

    RevisionDateProvider(ProcessRunner runner) {
        this.runner = runner;
    }

    public Date getDate(String rev) {
        String gitOutput = runner.run("git", "log", "--pretty=%ad", "--date=iso", rev, "-n", "1");
        //TODO process runner needs to fail on error here.
        // Otherwise the 'date' variable holds some error message and not really any date.
        //Example output returned by running git command: 2017-01-29 08:14:09 -0800
        return parseDate(gitOutput.trim());
    }
}

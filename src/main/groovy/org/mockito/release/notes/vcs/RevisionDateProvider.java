package org.mockito.release.notes.vcs;

import org.mockito.release.exec.ProcessRunner;

import java.util.Date;
import java.util.regex.Pattern;

import static org.mockito.release.notes.internal.DateFormat.parseDate;

/**
 * Provides date of given vcs revision
 */
class RevisionDateProvider {

    private final ProcessRunner runner;
    /**
     * pattern for validating revision date in ISO format
     * Example of valid date: 2017-01-29 08:14:09 -0800
     */
    private final Pattern REVISION_DATE_PATTERN = Pattern.compile(
            "\\s?\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\s[-+]\\d{4}\\s?");

    RevisionDateProvider(ProcessRunner runner) {
        this.runner = runner;
    }

    public Date getDate(String rev) {
        String gitOutput = runner.run("git", "log", "--pretty=%ad", "--date=iso", rev, "-n", "1");
        if(!REVISION_DATE_PATTERN.matcher(gitOutput).matches()){
           throw new IllegalArgumentException(formatErrorMessage(rev, gitOutput));
        }

        return parseDate(gitOutput.trim());
    }

    private String formatErrorMessage(String rev, String gitOutput) {
        return "Can't get a proper date for revision number " + rev +
                ". Are you sure this revision or tag exists?" +
                " Following output was returned by git:\n" + gitOutput;
    }
}

package org.mockito.release.notes.vcs;

import org.mockito.release.exec.ProcessRunner;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.release.notes.internal.DateFormat.parseDate;

class DefaultReleaseDateProvider implements ReleaseDateProvider {

    private final ProcessRunner runner;

    DefaultReleaseDateProvider(ProcessRunner runner) {
        this.runner = runner;
    }

    @Override
    public Map<String, Date> getReleaseDates(Collection<String> versions, String tagPrefix) {
        Map<String, Date> out = new HashMap<String, Date>();
        for (String version: versions) {
            String tag = tagPrefix + version;
            String date = runner.run("git", "log", "--pretty=%ad", "--date=iso", tag, "-n", "1");
            //TODO process runner needs to fail on error here.
            // Otherwise the 'date' variable holds some error message and not really any date.
            //Example output returned by running git command: 2017-01-29 08:14:09 -0800
            Date d = parseDate(date.trim());
            out.put(version, d);
        }

        return out;
    }
}

package org.mockito.release.notes.format;

import org.mockito.release.notes.model.VersionNotesData;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Tabular formatter
 *
 * IN PROGRESS
 */
public class TabularFormatter implements SingleReleaseNotesFormatter {

    @Override
    public String formatVersion(VersionNotesData data) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        String now = f.format(data.getDate());

        return "### " + data.getVersion() + " (" + now + ")";
    }
}

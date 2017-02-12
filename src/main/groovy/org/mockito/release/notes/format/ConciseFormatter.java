package org.mockito.release.notes.format;

import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.VersionNotesData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

class ConciseFormatter implements MultiReleaseNotesFormatter {

    public String formatReleaseNotes(Iterable<VersionNotesData> data) {
        StringBuilder sb = new StringBuilder();
        for (VersionNotesData d : data) {
            sb.append("### ").append(d.getVersion()).append(" - ").append(formatDate(d.getDate()))
                    .append("\n\n");

            for (Improvement i : d.getImprovements()) {
                //TODO SF let's add an author to every improvement here, at the end
                sb.append(" * ").append(CommonFormatting.format(i)).append("\n");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    private static String formatDate(Date date) {
        //TODO SF reuse and unify
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return f.format(date);
    }
}

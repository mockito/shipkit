package org.mockito.release.notes.format;

import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

class ConciseFormatter implements MultiReleaseNotesFormatter {

    private final String introductionText;

    public ConciseFormatter(String introductionText) {
        this.introductionText = introductionText;
    }

    public String formatReleaseNotes(Iterable<ReleaseNotesData> data) {
        StringBuilder sb = new StringBuilder(introductionText);
        for (ReleaseNotesData d : data) {
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

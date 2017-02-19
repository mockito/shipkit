package org.mockito.release.notes.format;

import org.mockito.release.notes.internal.DateFormat;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.util.Collection;
import java.util.Map;

class DetailedFormatter implements MultiReleaseNotesFormatter {

    private final String introductionText;
    private final Map<String, String> labelMapping;
    private final String vcsCommitsLinkTemplate;

    DetailedFormatter(String introductionText, Map<String, String> labelMapping, String vcsCommitsLinkTemplate) {
        this.introductionText = introductionText;
        this.labelMapping = labelMapping;
        this.vcsCommitsLinkTemplate = vcsCommitsLinkTemplate;
    }

    @Override
    public String formatReleaseNotes(Collection<ReleaseNotesData> data) {
        StringBuilder sb = new StringBuilder(introductionText == null? "": introductionText);
        if (data.isEmpty()) {
            sb.append("No release information.");
            return sb.toString();
        }

        for (ReleaseNotesData d : data) {
            sb.append("**").append(d.getVersion()).append("** - ");
            if (d.getContributions().getContributions().isEmpty()) {
                sb.append("no code changes (no commits) - ");
            }
            sb.append("*").append(DateFormat.formatDate(d.getDate())).append("*\n\n");
        }

        return sb.toString().trim();
    }
}

package org.mockito.release.notes.format;

import org.mockito.release.notes.internal.DateFormat;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;

class ConciseFormatter implements MultiReleaseNotesFormatter {

    private final String introductionText;

    public ConciseFormatter(String introductionText) {
        this.introductionText = introductionText;
    }

    public String formatReleaseNotes(Iterable<ReleaseNotesData> data) {
        StringBuilder sb = new StringBuilder(introductionText);
        for (ReleaseNotesData d : data) {
            sb.append("### ").append(d.getVersion()).append(" - ").append(DateFormat.formatDate(d.getDate()))
                    .append("\n\n");

            String contributions = formatContributions(d.getContributions(), d.getImprovements().size());
            sb.append(contributions).append("\n\n");

            for (Improvement i : d.getImprovements()) {
                //TODO SF let's add an author to every improvement here, at the end
                sb.append(" * ").append(CommonFormatting.format(i)).append("\n");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    private static String formatContributions(ContributionSet contributions, int improvementCount) {
        return "Authors: " + contributions.getAuthorCount()
                + ", commits: " + contributions.getAllCommits().size()
                + ", improvements: " + improvementCount + ".";
    }
}

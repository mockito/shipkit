package org.mockito.release.notes.format;

import org.mockito.release.notes.internal.DateFormat;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;

class ConciseFormatter implements MultiReleaseNotesFormatter {

    private final String introductionText;
    private final String detailedReleaseNotesLink;

    public ConciseFormatter(String introductionText, String detailedReleaseNotesLink) {
        this.introductionText = introductionText;
        this.detailedReleaseNotesLink = detailedReleaseNotesLink;
    }

    public String formatReleaseNotes(Iterable<ReleaseNotesData> data) {
        StringBuilder sb = new StringBuilder(introductionText);
        for (ReleaseNotesData d : data) {
            sb.append("### ").append(d.getVersion()).append(" - ").append(DateFormat.formatDate(d.getDate()))
                    .append("\n\n");

            //TODO SF make the link configurable
            String vcsCommitsLink = "https://github.com/mockito/mockito/compare/"
                    + d.getPreviousVersionVcsTag() + "..." + d.getVcsTag();

            String contributions = formatContributions(d.getContributions(), d.getImprovements().size(), detailedReleaseNotesLink, vcsCommitsLink);
            sb.append(contributions).append("\n\n");

            for (Improvement i : d.getImprovements()) {
                sb.append(" * ").append(CommonFormatting.format(i)).append("\n");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    private static String formatContributions(ContributionSet contributions, int improvementCount,
                                              String detailedReleaseNotesLink, String vcsCommitsLink) {
        return "Authors: [" + contributions.getAuthorCount() + "](" + detailedReleaseNotesLink + ")"
                + ", commits: [" + contributions.getAllCommits().size() + "](" + vcsCommitsLink + ")"
                + ", improvements: [" + improvementCount + "](" + detailedReleaseNotesLink + ").";
    }
}

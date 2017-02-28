package org.mockito.release.notes.format;

import org.mockito.release.notes.internal.DateFormat;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.text.MessageFormat;
import java.util.Collection;

class NotableFormatter implements MultiReleaseNotesFormatter {

    private final String introductionText;
    private final String detailedReleaseNotesLink;
    private final String vcsCommitsLinkTemplate;

    public NotableFormatter(String introductionText, String detailedReleaseNotesLink, String vcsCommitsLinkTemplate) {
        this.introductionText = introductionText;
        this.detailedReleaseNotesLink = detailedReleaseNotesLink;
        this.vcsCommitsLinkTemplate = vcsCommitsLinkTemplate;
    }

    public String formatReleaseNotes(Collection<ReleaseNotesData> data) {
        StringBuilder sb = new StringBuilder(introductionText == null? "":introductionText);
        for (ReleaseNotesData d : data) {
            sb.append("### ").append(d.getVersion()).append(" - ").append(DateFormat.formatDate(d.getDate()))
                    .append("\n\n");

            if (d.getContributions().getAllCommits().isEmpty()) {
                sb.append("No code changes. No commits found.\n");
            } else {
                //TODO SF formatter should not create link. Rather, the link should be a part of release notes data.
                //This is not only cleaner but also avoid duplication of link formatting across formatters.
                String vcsCommitsLink = MessageFormat.format(vcsCommitsLinkTemplate, d.getPreviousVersionVcsTag(), d.getVcsTag());

                String contributions = formatContributions(d.getContributions(), d.getImprovements().size(), detailedReleaseNotesLink, vcsCommitsLink);
                sb.append(contributions).append("\n\n");

                if (d.getImprovements().isEmpty()) {
                    sb.append("No notable improvements. No pull requests were referenced from [commits](")
                            .append(vcsCommitsLink)
                            .append(").\n");
                } else {
                    for (Improvement i : d.getImprovements()) {
                        sb.append(" * ").append(CommonFormatting.format(i)).append("\n");
                    }
                }
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

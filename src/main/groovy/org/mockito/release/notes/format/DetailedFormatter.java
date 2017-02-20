package org.mockito.release.notes.format;

import org.mockito.release.notes.internal.DateFormat;
import org.mockito.release.notes.model.Contribution;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

class DetailedFormatter implements MultiReleaseNotesFormatter {

    private static final int MAX_AUTHORS = 3;
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
            String vcsCommitsLink = MessageFormat.format(vcsCommitsLinkTemplate, d.getPreviousVersionVcsTag(), d.getVcsTag());
            sb.append(releaseHeadline(d.getContributions(), vcsCommitsLink));
            sb.append(" - *").append(DateFormat.formatDate(d.getDate())).append("*\n");

            if (!d.getContributions().getContributions().isEmpty()) {
                //no point printing any improvements information if there are no code changes
                sb.append(formatImprovements(d.getImprovements()));
            }

            sb.append("\n");
        }

        return sb.toString().trim();
    }

    static String formatImprovements(Collection<Improvement> improvements) {
        if (improvements.isEmpty()) {
            return ":cocktail: No pull requests referenced in commit messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Improvement i : improvements) {
            sb.append(":cocktail: ").append(i.getTitle())
                    .append(" [(#").append(i.getId()).append(")](")
                    .append(i.getUrl()).append(")").append("\n");
        }
        return sb.toString().trim();
    }

    static String releaseHeadline(ContributionSet contributions, String vcsCommitsLink) {
        if (contributions.getContributions().isEmpty()) {
            return "no code changes (no commits)";
        }
        StringBuilder sb = new StringBuilder();
        String commits = pluralize(contributions.getAllCommits().size(), "commit");
        String linkedCommits = link(commits, vcsCommitsLink);
        sb.append(linkedCommits).append(" by ").append(allAuthors(contributions));
        return sb.toString();
    }

    private static String link(String text, String link) {
        return "[" + text + "](" + link + ")";
    }

    private static String allAuthors(ContributionSet contributions) {
        if (contributions.getContributions().size() <= MAX_AUTHORS) {
            //if there is little authors, we just print them by name
            StringBuilder sb = new StringBuilder();
            for (Contribution c : contributions.getContributions()) {
                sb.append(c.getAuthorName()).append(", ");
            }
            return sb.substring(0, sb.length() - 2); //lose trailing ", "
        }
        //if there are many authors, we just write the total
        return "" + contributions.getAuthorCount() + " authors";
    }

    private static String pluralize(int size, String singularNoun) {
        return "" + size + " " + ((size == 1)? singularNoun : singularNoun + "s");
    }
}

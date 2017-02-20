package org.mockito.release.notes.format;

import org.mockito.release.notes.internal.DateFormat;
import org.mockito.release.notes.model.Contribution;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.text.MessageFormat;
import java.util.*;

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
            sb.append(releaseSummary(d.getDate(), d.getContributions(), vcsCommitsLink));

            if (!d.getContributions().getContributions().isEmpty()) {
                //no point printing any improvements information if there are no code changes
                sb.append(formatImprovements(d.getImprovements(), labelMapping));
            }

            sb.append("\n");
        }

        return sb.toString().trim();
    }

    static String releaseSummary(Date date, ContributionSet contributions, String vcsCommitsLink) {
        return authorsSummary(contributions, vcsCommitsLink) +
                " - *" + DateFormat.formatDate(date) + "*\n" +
                authorsSummaryAppendix(contributions);
    }

    private static String authorsSummaryAppendix(ContributionSet contributions) {
        StringBuilder sb = new StringBuilder();
        //add extra information about authors when there are many of them
        if (contributions.getAuthorCount() > MAX_AUTHORS) {
            sb.append(":cocktail: Commits: ").append(itemizedAuthors(contributions));
        }
        return sb.toString();
    }

    static String formatImprovements(Collection<Improvement> improvements, Map<String, String> labelMapping) {
        if (improvements.isEmpty()) {
            return ":cocktail: No pull requests referenced in commit messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Improvement i : improvements) {
            sb.append(":cocktail: ").append(labelMapping(i.getLabels(), labelMapping))
                    .append(i.getTitle())
                    .append(" [(#").append(i.getId()).append(")](")
                    .append(i.getUrl()).append(")").append("\n");
        }
        return sb.toString().trim();
    }

    //TODO SF add more unit test coverage
    static String labelMapping(Collection<String> labels, Map<String, String> labelMapping) {
        Set<String> labelSet = new HashSet<String>(labels);
        for (String mappingKey : labelMapping.keySet()) {
            if (labelSet.contains(mappingKey)) {
                return "[" + labelMapping.get(mappingKey) + "] ";
            }
        }
        return "";
    }

    static String authorsSummary(ContributionSet contributions, String vcsCommitsLink) {
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
            return itemizedAuthors(contributions);
        }
        //if there are many authors, we just write the total
        return "" + contributions.getAuthorCount() + " authors";
    }

    private static String itemizedAuthors(ContributionSet contributions) {
        StringBuilder sb = new StringBuilder();
        boolean showIndividualCommits = contributions.getAuthorCount() > 1;
        for (Contribution c : contributions.getContributions()) {
            sb.append(c.getAuthorName());
            if (showIndividualCommits) {
                sb.append(" (").append(c.getCommits().size()).append(")");
            }
            sb.append(", ");
        }
        return sb.substring(0, sb.length() - 2); //lose trailing ", "
    }

    private static String pluralize(int size, String singularNoun) {
        return "" + size + " " + ((size == 1)? singularNoun : singularNoun + "s");
    }
}

package org.mockito.release.notes.format;

import org.mockito.release.internal.util.MultiMap;
import org.mockito.release.notes.internal.DateFormat;
import org.mockito.release.notes.model.Contribution;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.text.MessageFormat;
import java.util.*;

class DetailedFormatter implements MultiReleaseNotesFormatter {

    private static final int MAX_AUTHORS = 3;
    private static final String NO_LABEL = "Remaining changes";
    private final String introductionText;
    private final Map<String, String> labelMapping;
    private final String vcsCommitsLinkTemplate;
    private final String publicationRepository;

    DetailedFormatter(String introductionText, Map<String, String> labelMapping, String vcsCommitsLinkTemplate,
                      String publicationRepository) {
        this.introductionText = introductionText;
        this.labelMapping = labelMapping;
        this.vcsCommitsLinkTemplate = vcsCommitsLinkTemplate;
        this.publicationRepository = publicationRepository;
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
            sb.append(releaseSummary(d.getDate(), d.getContributions(), vcsCommitsLink, publicationRepository));

            if (!d.getContributions().getContributions().isEmpty()) {
                //no point printing any improvements information if there are no code changes
                sb.append(formatImprovements(d.getImprovements(), labelMapping));
            }

            sb.append("\n");
        }

        return sb.toString().trim();
    }

    static String releaseSummary(Date date, ContributionSet contributions, String vcsCommitsLink, String publicationRepository) {
        return authorsSummary(contributions, vcsCommitsLink) +
                " - *" + DateFormat.formatDate(date) + "*" + " - published to " + publicationRepository + "\n" +
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
        MultiMap<String, Improvement> sorted = sortImprovements(improvements, labelMapping);

        for (String label: sorted.keySet()) {
            for (Improvement i : sorted.get(label)) {
                String labelPrefix = label.equals(NO_LABEL)? "":"[" + label + "] ";
                sb.append(":cocktail: ").append(labelPrefix).append(formatImprovement(i)).append("\n");
            }
        }

        return sb.toString().trim();
    }

    private static String formatImprovement(Improvement i) {
        return i.getTitle() +
                " [(#" + i.getId() + ")](" +
                i.getUrl() + ")";
    }

    private static MultiMap<String, Improvement> sortImprovements(Collection<Improvement> improvements, Map<String, String> labelMapping) {
        MultiMap<String, Improvement> byLabel = new MultiMap<String, Improvement>();
        Set<Improvement> remainingImprovements = new LinkedHashSet<Improvement>(improvements);

        //Step 1, find improvements that match input labels
        //Iterate label first because the input labels determine the order
        for (String label : labelMapping.keySet()) {
            for (Improvement i : improvements) {
                if (i.getLabels().contains(label) && remainingImprovements.contains(i)) {
                    remainingImprovements.remove(i);
                    byLabel.put(labelMapping.get(label), i);
                }
            }
        }

        //Step 2, add remaining improvements
        for (Improvement i : remainingImprovements) {
            byLabel.put(NO_LABEL, i);
        }

        return byLabel;
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
            sb.append(authorLink(c));
            if (showIndividualCommits) {
                sb.append(" (").append(c.getCommits().size()).append(")");
            }
            sb.append(", ");
        }
        return sb.substring(0, sb.length() - 2); //lose trailing ", "
    }

    static String authorLink(Contribution c) {
        if (c.getContributor() == null) {
            return c.getAuthorName();
        } else {
            return "[" + c.getAuthorName() + "](" + c.getContributor().getProfileUrl() + ")";
        }
    }

    private static String pluralize(int size, String singularNoun) {
        return "" + size + " " + ((size == 1)? singularNoun : singularNoun + "s");
    }
}

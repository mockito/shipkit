package org.shipkit.internal.notes.format;

import org.shipkit.internal.gradle.util.StringUtil;
import org.shipkit.internal.util.DateUtil;
import org.shipkit.internal.util.MultiMap;
import org.shipkit.internal.notes.model.*;

import java.text.MessageFormat;
import java.util.*;

/**
 * Generates release notes. The class is hard to maintain. If you have ideas on how to make it cleaner, go for it
 */
class DetailedFormatter implements MultiReleaseNotesFormatter {

    private static final int MAX_AUTHORS = 3;
    private static final String NO_LABEL = "Remaining changes";
    private final String introductionText;
    private final Map<String, String> labelMapping;
    private final String vcsCommitsLinkTemplate;
    private final String publicationRepository;
    private final Map<String, Contributor> contributors;
    private final boolean emphasizeVersion;
    private final String header;

    DetailedFormatter(String header, String introductionText, Map<String, String> labelMapping, String vcsCommitsLinkTemplate,
                      String publicationRepository, Map<String, Contributor> contributors, boolean emphasizeVersion) {
        this.header = header;
        this.introductionText = introductionText;
        this.labelMapping = labelMapping;
        this.vcsCommitsLinkTemplate = vcsCommitsLinkTemplate;
        this.publicationRepository = publicationRepository;
        this.contributors = contributors;
        this.emphasizeVersion = emphasizeVersion;
    }

    @Override
    public String formatReleaseNotes(Collection<ReleaseNotesData> data) {
        StringBuilder sb = new StringBuilder(header);
        sb.append(introductionText == null ? "" : introductionText);
        if (data.isEmpty()) {
            sb.append("No release information.");
            return sb.toString();
        }

        for (ReleaseNotesData d : data) {
            sb.append(header(d.getVersion(), d.getDate(), emphasizeVersion));
            String vcsCommitsLink = MessageFormat.format(vcsCommitsLinkTemplate, d.getPreviousVersionVcsTag(), d.getVcsTag());
            sb.append(releaseSummary(d.getVersion(), d.getDate(), d.getContributions(), contributors, vcsCommitsLink, publicationRepository));

            if (!d.getContributions().getContributions().isEmpty()) {
                //no point printing any improvements information if there are no code changes
                sb.append(formatImprovements(d.getImprovements(), labelMapping));
            }

            sb.append("\n");
        }

        return sb.toString().trim();
    }

    static String header(String version, Date date, boolean emphasizeVersion) {
        return emphasizeVersion ? buildHeader(version, date, "# ", "")
                : buildHeader(version, date, "**", "**");
    }

    private static String buildHeader(String version, Date date, String prefix, String postfix) {
        return prefix + version + " (" + DateUtil.formatDate(date) + ")" + postfix + " - ";
    }

    static String releaseSummary(String version, Date date, ContributionSet contributions, Map<String, Contributor> contributors,
                                 String vcsCommitsLink, String publicationRepository) {
        return authorsSummary(contributions, contributors, vcsCommitsLink) +
                " - published to " + getBintrayBadge(version, publicationRepository) + "\n" +
                authorsSummaryAppendix(contributions, contributors);
    }

    private static String getBintrayBadge(String version, String publicationRepository) {
        final String markdownPrefix = "[![Bintray](";
        final String shieldsIoBadgeLink = "https://img.shields.io/badge/Bintray-" + version + "-green.svg";
        final String markdownPostfix = ")]";
        final String repositoryLinkWithVersion = publicationRepository + "/" + version;
        return markdownPrefix + shieldsIoBadgeLink + markdownPostfix + "(" + repositoryLinkWithVersion + ")";
    }

    private static String authorsSummaryAppendix(ContributionSet contributions, Map<String, Contributor> contributors) {
        StringBuilder sb = new StringBuilder();
        //add extra information about authors when there are many of them
        if (contributions.getAuthorCount() > MAX_AUTHORS) {
            sb.append(" - Commits: ").append(itemizedAuthors(contributions, contributors)).append("\n");
        }
        return sb.toString();
    }

    static String formatImprovements(Collection<Improvement> improvements, Map<String, String> labelMapping) {
        if (improvements.isEmpty()) {
            return " - No pull requests referenced in commit messages.";
        }

        StringBuilder sb = new StringBuilder();
        MultiMap<String, Improvement> sorted = sortImprovements(improvements, labelMapping);

        for (String label: sorted.keySet()) {
            for (Improvement i : sorted.get(label)) {
                String labelPrefix = label.equals(NO_LABEL) ? "" : "[" + label + "] ";
                sb.append(" - ").append(labelPrefix).append(formatImprovement(i)).append("\n");
            }
        }

        return " " + sb.toString().trim();
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

    static String authorsSummary(ContributionSet contributions, Map<String, Contributor> contributors, String vcsCommitsLink) {
        if (contributions.getContributions().isEmpty()) {
            return "no code changes (no commits)";
        }
        StringBuilder sb = new StringBuilder();
        String commits = pluralize(contributions.getAllCommits().size(), "commit");
        String linkedCommits = link(commits, vcsCommitsLink);
        sb.append(linkedCommits).append(" by ").append(allAuthors(contributions, contributors));
        return sb.toString();
    }

    private static String link(String text, String link) {
        return StringUtil.isEmpty(link) ? text :
                "[" + text + "](" + link + ")";
    }

    private static String allAuthors(ContributionSet contributions, Map<String, Contributor> contributors) {
        if (contributions.getAuthorCount() <= MAX_AUTHORS) {
            //if there is little authors, we just print them by name
            return itemizedAuthors(contributions, contributors);
        }
        //if there are many authors, we just write the total
        return "" + contributions.getAuthorCount() + " authors";
    }

    private static String itemizedAuthors(ContributionSet contributions, Map<String, Contributor> contributors) {
        StringBuilder sb = new StringBuilder();
        boolean showIndividualCommits = contributions.getAuthorCount() > 1;
        for (Contribution c : contributions.getContributions()) {
            sb.append(authorLink(c, contributors.get(c.getAuthorName())));
            if (showIndividualCommits) {
                sb.append(" (").append(c.getCommits().size()).append(")");
            }
            sb.append(", ");
        }
        return sb.substring(0, sb.length() - 2); //lose trailing ", "
    }

    static String authorLink(Contribution c, Contributor author) {
        if (author == null) {
            //TODO if the author name does not have spaces it indicates that this could be GitHub id
            // that we could automatically try to use
            // At worst what can happen is that we will have bad links for scenarios where we did not have any links before
            // But we will solve some cases, for example 'epeee' - user without name but with id ;)
            return c.getAuthorName();
        } else {
            return "[" + c.getAuthorName() + "](" + author.getProfileUrl() + ")";
        }
    }

    private static String pluralize(int size, String singularNoun) {
        return "" + size + " " + ((size == 1) ? singularNoun : singularNoun + "s");
    }
}

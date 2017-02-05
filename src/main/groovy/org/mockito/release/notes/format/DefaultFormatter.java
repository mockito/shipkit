package org.mockito.release.notes.format;

import org.mockito.release.notes.improvements.DefaultImprovement;
import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.notes.vcs.DefaultContribution;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.util.MultiMap;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Original formatter
 */
public class DefaultFormatter implements ReleaseNotesFormatter {

    private String format(DefaultImprovement improvement) {
        return improvement.getTitle() + " [(#" + improvement.getId() + ")](" + improvement.getUrl() + ")";
    }

    String format(Map<String, String> labels, Collection<DefaultImprovement> improvements) {
        if (improvements.isEmpty()) {
            return "* No notable improvements. See the commits for detailed changes.";
        }
        StringBuilder sb = new StringBuilder("* Improvements: ").append(improvements.size());
        MultiMap<String, DefaultImprovement> byLabel = new MultiMap<String, DefaultImprovement>();
        Set<DefaultImprovement> remainingImprovements = new LinkedHashSet<DefaultImprovement>(improvements);

        //Step 1, find improvements that match input labels
        //Iterate label first because the input labels determine the order
        for (String label : labels.keySet()) {
            for (DefaultImprovement i : improvements) {
                if (i.getLabels().contains(label) && remainingImprovements.contains(i)) {
                    remainingImprovements.remove(i);
                    byLabel.put(label, i);
                }
            }
        }

        //Step 2, print out the improvements that match input labels
        for (String label : byLabel.keySet()) {
            String labelCaption = labels.get(label);
            Collection<DefaultImprovement> labelImprovements = byLabel.get(label);
            sb.append("\n  * ").append(labelCaption).append(": ").append(labelImprovements.size());
            for (DefaultImprovement i : labelImprovements) {
                sb.append("\n    * ").append(format(i));
            }
        }

        //Step 3, print out remaining changes
        if (!remainingImprovements.isEmpty()) {
            String indent;
            //We want clean view depending if there are labelled improvements or not
            if (byLabel.size() > 0) {
                indent = "  ";
                sb.append("\n  * Remaining changes: ").append(remainingImprovements.size());
            } else {
                indent = "";
            }

            for (DefaultImprovement i : remainingImprovements) {
                sb.append("\n").append(indent).append("  * ").append(format(i));
            }
        }
        return sb.toString();
    }

    private String format(DefaultContribution contribution) {
        return contribution.getCommits().size() + ": " + contribution.getAuthorName();
    }

    private String format(ContributionSet contributions) {
        StringBuilder sb = new StringBuilder("* Authors: ").append(contributions.getContributions().size())
                .append("\n* Commits: ").append(contributions.getAllCommits().size());

        for (DefaultContribution c : contributions.getContributions()) {
            sb.append("\n  * ").append(format(c));
        }

        return sb.toString();
    }

    @Override
    public String formatNotes(ReleaseNotesData data) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String now = format.format(data.getDate());

        return "### " + data.getVersion() + " (" + now + ")" + "\n\n"
                + format(data.getContributions()) + "\n"
                + format(data.getLabels(), data.getImprovements()) + "\n\n";
    }
}

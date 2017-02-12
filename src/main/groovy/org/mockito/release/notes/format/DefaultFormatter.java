package org.mockito.release.notes.format;

import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.VersionNotesData;
import org.mockito.release.notes.model.ReleaseNotesFormat;
import org.mockito.release.notes.vcs.DefaultContribution;
import org.mockito.release.util.MultiMap;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Original formatter
 */
public class DefaultFormatter implements ReleaseNotesFormatter {

    private String format(Improvement improvement) {
        return improvement.getTitle() + " [(#" + improvement.getId() + ")](" + improvement.getUrl() + ")";
    }

    public String format(Map<String, String> labels, Collection<Improvement> improvements) {
        if (improvements.isEmpty()) {
            return "* No notable improvements. See the commits for detailed changes.";
        }
        StringBuilder sb = new StringBuilder("* Improvements: ").append(improvements.size());
        MultiMap<String, Improvement> byLabel = new MultiMap<String, Improvement>();
        Set<Improvement> remainingImprovements = new LinkedHashSet<Improvement>(improvements);

        //Step 1, find improvements that match input labels
        //Iterate label first because the input labels determine the order
        for (String label : labels.keySet()) {
            for (Improvement i : improvements) {
                if (i.getLabels().contains(label) && remainingImprovements.contains(i)) {
                    remainingImprovements.remove(i);
                    byLabel.put(label, i);
                }
            }
        }

        //Step 2, print out the improvements that match input labels
        for (String label : byLabel.keySet()) {
            String labelCaption = labels.get(label);
            Collection<Improvement> labelImprovements = byLabel.get(label);
            sb.append("\n  * ").append(labelCaption).append(": ").append(labelImprovements.size());
            for (Improvement i : labelImprovements) {
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

            for (Improvement i : remainingImprovements) {
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
    public String formatNotes(VersionNotesData data, ReleaseNotesFormat format) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        String now = f.format(data.getDate());

        return "### " + data.getVersion() + " (" + now + ")" + "\n\n"
                + format(data.getContributions()) + "\n"
                + format(format.getLabelMapping(), data.getImprovements()) + "\n\n";
    }
}

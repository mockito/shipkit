package org.shipkit.internal.gradle.release.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;
import java.util.List;

class ComparisonResults {

    private final static Logger LOG = Logging.getLogger(ComparisonResults.class);

    private final String description;
    private final boolean resultsIdentical;

    ComparisonResults(List<File> comparisonResults) {
        int comparisons = 0;
        StringBuilder sb = new StringBuilder();
        for (File result : comparisonResults) {
            if (result.isFile()) {
                comparisons++;
                LOG.info("Looking for diffs in publication comparison result file: " + result);
                if (result.length() > 0) {
                    //file contains differences
                    sb.append(IOUtil.readFully(result));
                }
            }
        }

        if (sb.length() > 0) {
            description = "\n  Compared " + comparisons + " publication(s). Changes since previous release:\n" + sb;
            resultsIdentical = false;
        } else if (comparisons > 0) {
            description = "\n  Compared " + comparisons + " publication(s). No changes since previous release!";
            resultsIdentical = true;
        } else {
            description = "\n  Publication comparison was skipped (no comparison result files found).";
            resultsIdentical = false;
        }
    }

    boolean areResultsIdentical() {
        return resultsIdentical;
    }

    String getDescription() {
        return description;
    }
}

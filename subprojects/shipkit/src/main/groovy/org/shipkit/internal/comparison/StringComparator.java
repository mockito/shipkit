package org.shipkit.internal.comparison;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.comparison.diff.Diff;
import org.shipkit.internal.comparison.diff.FileDiffGenerator;

import static org.shipkit.internal.util.ArgumentValidation.notNull;

public class StringComparator {

    private static final Logger LOG = Logging.getLogger(StringComparator.class);

    public Diff areEqual(String previousContent, String currentContent) {
        notNull(
            previousContent, "previous content to compare",
            currentContent, "current content to compare");

        boolean areEqual = previousContent.equals(currentContent);

        LOG.debug("Comparison of :\n\n"  +
            "  -- previousVersion: \n{} \n\n" +
            "  -- currentVersion: \n{} \n",
            previousContent, currentContent
        );

        if (!areEqual) {
            String diffOutput =
                new FileDiffGenerator().generateDiff(
                    previousContent,
                    currentContent
                );

            return Diff.ofDifferentFiles(diffOutput);
        }

        return Diff.ofEqualFiles();
    }
}

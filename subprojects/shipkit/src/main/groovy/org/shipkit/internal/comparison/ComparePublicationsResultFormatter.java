package org.shipkit.internal.comparison;

import org.shipkit.internal.comparison.diff.Diff;
import org.shipkit.internal.gradle.java.tasks.ComparePublications;

import java.io.File;

/**
 * Creates an aggregated comparison result, for publication comparison of sources jars and dependency-info.md files.
 * See {@link ComparePublications} for details.
 */
public class ComparePublicationsResultFormatter {

    public static final String NO_DIFFERENCES = "    No differences.";
    public static final String LONG_INDENT = "    ";
    public static final String SHORT_INDENT = "  ";

    public String formatResults(File previousSourcesJar, File currentSourcesJar,
                                Diff sourcesJarDiff, Diff dependencyInfoFilesDiff) {

        String result = "";

        // add diff between sources jars
        if (!sourcesJarDiff.areFilesEqual()) {
            result += getFileComparisonHeader(previousSourcesJar.getAbsolutePath(), currentSourcesJar.getAbsolutePath());
            result +=  sourcesJarDiff.getDiffOutput();
        }

        if (!sourcesJarDiff.areFilesEqual() && !dependencyInfoFilesDiff.areFilesEqual()) {
            result += "\n\n";
        }

        // add diff between dependency-info.md files
        if (!dependencyInfoFilesDiff.areFilesEqual()) {
            result += getFileComparisonHeader(getDependencyInfoFilePath(previousSourcesJar), getDependencyInfoFilePath(currentSourcesJar));
            result += LONG_INDENT + "Here you can see the changes in declared runtime dependencies between versions.\n\n";
            result += dependencyInfoFilesDiff.getDiffOutput();
        }

        return result;
    }

    private String getDependencyInfoFilePath(File jar) {
        return jar.getAbsolutePath() + "/" + ComparePublications.DEPENDENCY_INFO_FILEPATH;
    }

    private String getFileComparisonHeader(String previousFilePath, String currentFilePath) {
        return SHORT_INDENT + "Differences between files:\n  --- "
             + previousFilePath + "\n"
             + SHORT_INDENT + "+++ " + currentFilePath + "\n\n";
    }
}

package org.shipkit.internal.comparison;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.comparison.diff.Diff;
import org.shipkit.internal.comparison.diff.FileDiffGenerator;
import org.shipkit.internal.gradle.java.tasks.ComparePublications;

import java.io.File;

import static org.shipkit.internal.util.ArgumentValidation.notNull;

public class DependencyInfoComparator {

    private static final Logger LOG = Logging.getLogger(DependencyInfoComparator.class);

    private final DependencyInfoFilter filter;

    public DependencyInfoComparator(String projectGroup, String previousVersion, String currentVersion) {
        notNull(projectGroup, "project group", previousVersion, "previous version",
                currentVersion, "current version");
        this.filter =
                new DependencyInfoFilter(projectGroup, previousVersion, currentVersion);
    }

    DependencyInfoComparator(DependencyInfoFilter filter) {
        this.filter = filter;
    }

    public Diff areEqual(File previousSourcesJar, File currentSourcesJar,
                         String previousFileContent, String currentFileContent) {
        notNull(previousFileContent, "previous dependency file to compare",
            currentFileContent, "current dependency file to compare");

        String previousFileName = previousSourcesJar.getAbsolutePath() + "/" + ComparePublications.DEPENDENCY_INFO_FILEPATH;
        String currentFileName = currentSourcesJar.getAbsolutePath() + "/" + ComparePublications.DEPENDENCY_INFO_FILEPATH;

        LOG.info("About to compare {} files from sources jars:\n\n " +
            "  -- for previous version: \n{}\n\n" +
            "  -- for current version: \n{} \n", ComparePublications.DEPENDENCY_INFO_FILEPATH, previousSourcesJar, currentSourcesJar);

        String filteredPreviousContent = filter.filter(previousFileContent);
        String filteredCurrentContent = filter.filter(currentFileContent);

        boolean areEqual = filteredPreviousContent.equals(filteredCurrentContent);

        LOG.debug("Comparison of declared dependencies:\n\n"  +
            "  -- previousVersion: \n{} \n\n" +
            "  -- currentVersion: \n{} \n",
            filteredPreviousContent, filteredCurrentContent
        );

        if (!areEqual) {
            String prefix = "  Here you can see the changes in declared dependencies between versions.\n\n";
            String diffOutput = prefix +
                new FileDiffGenerator().generateDiff(
                    "Previous " + ComparePublications.DEPENDENCY_INFO_FILEPATH,
                    "Current " + ComparePublications.DEPENDENCY_INFO_FILEPATH,
                    filteredPreviousContent,
                    filteredCurrentContent
                );

            return Diff.ofDifferentFiles(previousFileName, currentFileName, diffOutput);
        }

        return Diff.ofEqualFiles(previousFileName, currentFileName);
    }
}

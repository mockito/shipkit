package org.shipkit.internal.gradle.java.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.java.ComparePublicationsTask;
import org.shipkit.internal.comparison.DependencyInfoComparator;
import org.shipkit.internal.comparison.ZipComparator;
import org.shipkit.internal.comparison.diff.Diff;
import org.shipkit.internal.gradle.java.ComparePublicationsPlugin;
import org.shipkit.internal.gradle.java.JavaLibraryPlugin;
import org.shipkit.internal.gradle.util.ZipUtil;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;
import java.util.*;

public class ComparePublications {

    private final static Logger LOG = Logging.getLogger(ComparePublications.class);
    public static final String DEPENDENCY_INFO_FILEPATH = "META-INF/" + ComparePublicationsPlugin.DEPENDENCY_INFO_FILENAME;

    public void comparePublications(ComparePublicationsTask task) {
        if (!task.getPreviousSourcesJar().exists()) {
            LOG.lifecycle("{} - previous publications not found, nothing to compare, skipping", task.getPath());
            return;
        }

        //TODO let's add decent validation and descriptive error messages to the user
        assert task.getSourcesJar().getArchivePath().isFile();

        File currentVersionSourcesJarFile = task.getSourcesJar().getArchivePath();

        LOG.lifecycle("{} - about to compare publications",
                task.getPath());

        if (!ZipUtil.fileContainsEntry(task.getPreviousSourcesJar(), DEPENDENCY_INFO_FILEPATH)) {
            LOG.lifecycle("{} - previous {} file not found, nothing to compare, skipping", task.getPath(), DEPENDENCY_INFO_FILEPATH);
            return;
        }

        DependencyInfoComparator dependencyInfoComparator = new DependencyInfoComparator();
        Diff depInfoDiff = dependencyInfoComparator.areEqual(task.getPreviousSourcesJar(), currentVersionSourcesJarFile,
                ZipUtil.readEntryContent(task.getPreviousSourcesJar(), DEPENDENCY_INFO_FILEPATH),
                ZipUtil.readEntryContent(currentVersionSourcesJarFile, DEPENDENCY_INFO_FILEPATH));

        LOG.lifecycle("{} - {} files equal: {}", task.getPath(), DEPENDENCY_INFO_FILEPATH, depInfoDiff.areFilesEqual());

        ZipComparator sourcesJarComparator = new ZipComparator();
        Diff jarsDiff = sourcesJarComparator.areEqual(task.getPreviousSourcesJar(), currentVersionSourcesJarFile);
        LOG.lifecycle("{} - source jars equal: {}", task.getPath(), jarsDiff.areFilesEqual());

        List<Diff> differences = new ArrayList<Diff>();
        differences.add(jarsDiff);
        differences.add(depInfoDiff);

        StringBuilder comparisonResult = new StringBuilder();
        for (Diff diff : differences) {
            if (!diff.areFilesEqual()) {
                comparisonResult.append("  Differences between files:\n  --- ")
                        .append(diff.getPreviousFilePath()).append("\n")
                        .append("  +++ ").append(diff.getCurrentFilePath()).append("\n\n")
                        .append(diff.getDiffOutput()).append("\n");
            }
        }

        IOUtil.writeFile(task.getComparisonResult(), comparisonResult.toString());
    }

}

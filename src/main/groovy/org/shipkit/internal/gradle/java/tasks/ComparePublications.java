package org.shipkit.internal.gradle.java.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.publish.maven.tasks.GenerateMavenPom;
import org.shipkit.gradle.java.ComparePublicationsTask;
import org.shipkit.internal.comparison.PomComparator;
import org.shipkit.internal.comparison.ZipComparator;
import org.shipkit.internal.comparison.diff.Diff;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComparePublications {

    private final static Logger LOG = Logging.getLogger(ComparePublications.class);

    public Boolean comparePublications(ComparePublicationsTask task) {
        if (task.getPreviousVersion() == null) {
            LOG.lifecycle("{} - previousVersion is not set, nothing to compare", task.getPath());
            return false;
        }

        GenerateMavenPom pomTask = (GenerateMavenPom) task.getProject().getTasks().getByName(task.getPomTaskName());

        //TODO let's add decent validation and descriptive error messages to the user
        assert pomTask.getDestination().isFile();
        assert task.getSourcesJar().getArchivePath().isFile();

        File currentVersionPomFile = pomTask.getDestination();
        File currentVersionSourcesJarFile = task.getSourcesJar().getArchivePath();

        LOG.lifecycle("{} - about to compare publications, for versions {} and {}",
                task.getPath(), task.getPreviousVersion(), task.getCurrentVersion());

        PomComparator pomComparator = new PomComparator(task.getProjectGroup(), task.getPreviousVersion(), task.getCurrentVersion());
        Diff pomsDiff = pomComparator.areEqual(task.getPreviousVersionPomFile(), currentVersionPomFile);
        LOG.lifecycle("{} - pom files equal: {}", task.getPath(), pomsDiff.areFilesEqual());

        ZipComparator sourcesJarComparator = new ZipComparator();
        Diff jarsDiff = sourcesJarComparator.areEqual(task.getPreviousVersionSourcesJarFile(), currentVersionSourcesJarFile);
        LOG.lifecycle("{} - source jars equal: {}", task.getPath(), jarsDiff.areFilesEqual());

        List<Diff> differences = new ArrayList<Diff>();
        differences.add(jarsDiff);
        differences.add(pomsDiff);

        StringBuilder comparisonResult = new StringBuilder();
        for (Diff diff : differences) {
            if (!diff.areFilesEqual()) {
                comparisonResult.append("  Differences between files:\n  --- ")
                        .append(diff.getPreviousFile()).append("\n")
                        .append("  +++ ").append(diff.getCurrentFile()).append("\n\n")
                        .append(diff.getDiffOutput()).append("\n");
            }
        }

        IOUtil.writeFile(task.getComparisonResult(), comparisonResult.toString());

        return jarsDiff.areFilesEqual() && pomsDiff.areFilesEqual();
    }
}

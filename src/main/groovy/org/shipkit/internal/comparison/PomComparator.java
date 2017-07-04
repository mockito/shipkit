package org.shipkit.internal.comparison;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.comparison.diff.Diff;
import org.shipkit.internal.comparison.diff.FileDiffGenerator;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;

import static org.shipkit.internal.util.ArgumentValidation.notNull;

public class PomComparator{

    private static final Logger LOG = Logging.getLogger(PomComparator.class);

    private final PomFilter pomFilter;

    public PomComparator(String projectGroup, String previousVersion, String currentVersion) {
        notNull(projectGroup, "project group", previousVersion, "previous version",
                currentVersion, "current version");
        this.pomFilter =
                new PomFilter(projectGroup, previousVersion, currentVersion);
    }

    PomComparator(PomFilter pomFilter){
        this.pomFilter = pomFilter;
    }

    public Diff areEqual(File previousFile, File currentFile) {
        notNull(previousFile, "previous pom to compare", currentFile, "current pom to compare");
        LOG.info("About to compare pom files:\n\n " +
            "  -- previousVersionFile: \n{}\n\n" +
            "  -- currentVersionFile: \n{} \n", previousFile, currentFile);
        String previousContent = IOUtil.readFully(previousFile);
        String currentContent = IOUtil.readFully(currentFile);

        String filteredPreviousContent = pomFilter.filter(previousContent);
        String filteredCurrentContent = pomFilter.filter(currentContent);

        boolean areEqual = filteredPreviousContent.equals(filteredCurrentContent);

        LOG.debug("Content of pom comparison:\n\n"  +
            "  -- previousVersionFile: \n{} \n\n" +
            "  -- currentVersionFile: \n{} \n",
            filteredPreviousContent, filteredCurrentContent
        );

        if(!areEqual){
            String diffOutput = new FileDiffGenerator().generateDiff(previousFile.getAbsolutePath(), currentFile.getAbsolutePath(),
                                    filteredPreviousContent, filteredCurrentContent);

            return Diff.ofDifferentFiles(previousFile, currentFile, diffOutput);
        }

        return Diff.ofEqualFiles(previousFile, currentFile);
    }
}

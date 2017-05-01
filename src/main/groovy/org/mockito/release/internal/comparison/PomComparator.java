package org.mockito.release.internal.comparison;

import org.mockito.release.notes.util.IOUtil;

import java.io.File;

import static org.mockito.release.internal.util.ArgumentValidation.notNull;

class PomComparator implements FileComparator{

    private final PomFilter pomFilter;

    PomComparator(String projectGroup, String previousVersion, String currentVersion) {
        notNull(projectGroup, "project group", previousVersion, "previous version",
                currentVersion, "current version");
        this.pomFilter =
                new PomFilter(projectGroup, previousVersion, currentVersion);
    }

    PomComparator(PomFilter pomFilter){
        this.pomFilter = pomFilter;
    }

    public boolean areEqual(File leftFile, File rightFile) {
        notNull(leftFile, "pom content to compare", rightFile, "pom content to compare");
        String left = IOUtil.readFully(leftFile);
        String right = IOUtil.readFully(rightFile);

        String parsedLeft = pomFilter.filter(left);
        String parsedRight = pomFilter.filter(right);
        return parsedLeft.equals(parsedRight);
    }
}

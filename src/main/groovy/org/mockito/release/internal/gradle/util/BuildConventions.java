package org.mockito.release.internal.gradle.util;

import org.gradle.api.Project;

import java.io.File;

/**
 * Conventions and defaults used by the build
 */
public class BuildConventions {

    /**
     * Returns file object in a standard location where we put other output files generated during the build.
     */
    public static File outputFile(Project project, String fileName) {
        return new File(project.getBuildDir(), "/release-tools/" + fileName);
    }
}

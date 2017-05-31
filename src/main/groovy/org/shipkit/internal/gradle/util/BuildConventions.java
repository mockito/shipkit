package org.shipkit.internal.gradle.util;

import org.gradle.api.Project;

import java.io.File;

/**
 * Conventions and defaults used by the build
 */
public class BuildConventions {

    /**
     * Returns file object for storing contributors.
     */
    public static File contributorsFile(Project project) {
        return outputFile(project.getRootProject(), "all-contributors.json");
    }

    /**
     * Returns file object in a standard location where we put other output files generated during the build.
     */
    private static File outputFile(Project project, String fileName) {
        return new File(project.getBuildDir(), "/release-tools/" + fileName);
    }
}

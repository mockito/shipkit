package org.shipkit.internal.exec;

import java.io.File;

/**
 * Process execution services
 */
public class Exec {

    /**
     * Provides process runner for given working dir
     */
    public static ProcessRunner getProcessRunner(File workDir) {
        return new DefaultProcessRunner(workDir);
    }

    /**
     * Provides process runner for given working dir
     */
    public static ProcessRunner getProcessRunner(File workDir, File outputLogFile) {
        return new DefaultProcessRunner(workDir, outputLogFile);
    }
}

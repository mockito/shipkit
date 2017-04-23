package org.mockito.release.exec;

import java.util.List;

/**
 * Provides ways to execute external processes
 */
public interface ProcessRunner {

    /**
     * Executes given command line and returns the output.
     *
     * @param commandLine to execute
     * @return combined error and standard output.
     */
    String run(String ... commandLine);

    /**
     * Executes given command line and returns result.
     *
     * @param commandLine the full command line to execute
     * @return combined error and standard output.
     */
    String run(List<String> commandLine);
}

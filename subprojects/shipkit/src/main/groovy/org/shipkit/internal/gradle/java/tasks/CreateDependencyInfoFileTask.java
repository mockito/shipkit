package org.shipkit.internal.gradle.java.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.*;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

/**
 * Creates a file {@link #getOutputFile()} with information about all declared dependencies of the project.
 * Dependencies are taken from passed {@link #getConfiguration()}.
 *
 */
public class CreateDependencyInfoFileTask extends DefaultTask {

    @InputFiles
    private Configuration configuration;
    @OutputFile
    private File outputFile;

    @TaskAction public void createFile() {
        new CreateDependencyInfoFile().createDependencyInfoFile(this);
    }

    /**
     * Configuration from which all declared dependencies will be extracted.
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * See {@link #getConfiguration()}
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * File to which JSON output will be saved.
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * See {@link #getOutputFile()}
     */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

}

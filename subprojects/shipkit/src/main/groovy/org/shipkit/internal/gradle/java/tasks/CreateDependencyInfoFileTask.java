package org.shipkit.internal.gradle.java.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.*;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

/**
 * Creates a file {@link #getOutputFile()} with information about all declared dependencies of the project.
 * Dependencies are taken from passed {@link #getConfiguration()}.
 * Submodule dependencies that have the same version as {@link #getProjectVersion()} and group as {@link #getProjectGroup()}
 * are represented in the file without version, so that they won't come up as a difference when comparing publications.
 *
 */
public class CreateDependencyInfoFileTask extends DefaultTask {

    @InputFiles
    private Configuration configuration;
    @Input
    private String projectGroup;
    @Input
    private String projectVersion;
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

    /**
     * Current project group that will be used to determine submodules out of all declared runtime dependencies.
     */
    public String getProjectGroup() {
        return projectGroup;
    }

    /**
     * See {@link #getProjectGroup()}
     */
    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    /**
     * Current project version that will be used to determine submodules out of all declared runtime dependencies.
     */
    public String getProjectVersion() {
        return projectVersion;
    }

    /**
     * See {@link #getProjectVersion()}
     */
    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }
}

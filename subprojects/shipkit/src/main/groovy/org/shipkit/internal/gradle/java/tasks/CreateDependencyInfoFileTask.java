package org.shipkit.internal.gradle.java.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.*;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.version.VersionInfo;

import java.io.File;

/**
 * Creates a file {@link #getOutputFile()} with information about all resolved dependencies of the project.
 * Dependencies are taken from passed {@link #getConfiguration()}.
 * Results include:
 * - resolved dependencies
 * - files
 * - gradleApi()
 * - localGroovy()
 *
 * They don't include the sibling projects of the same version. Eg. if project A depends on sibling project B
 * with the {@link #getProjectGroup()} and {@link #getCurrentProjectVersion()}, B won't be included in the results.
 *
 */
public class CreateDependencyInfoFileTask extends DefaultTask {

    @InputFiles
    private Configuration configuration;
    @Input
    private String currentProjectVersion;
    @Input
    private String projectGroup;
    @OutputFile
    private File outputFile;

    @TaskAction public void createFile() {
        new CreateDependencyInfoFile().createDependencyInfoFile(this);
    }

    /**
     * Configuration from which resolved and self-resolving dependencies will be extracted.
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
     * See {@link Project#getGroup()}
     */
    public String getProjectGroup() {
        return projectGroup;
    }

    /**
     * See {@link Project#getGroup()}
     */
    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    /**
     * See {@link VersionInfo#getVersion()}
     */
    public String getCurrentProjectVersion() {
        return currentProjectVersion;
    }

    /**
     * See {@link VersionInfo#getVersion()}
     */
    public void setCurrentProjectVersion(String currentProjectVersion) {
        this.currentProjectVersion = currentProjectVersion;
    }
}

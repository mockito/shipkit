package org.shipkit.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;
import org.shipkit.internal.gradle.java.tasks.ComparePublications;

import java.io.File;

/**
 * Compares sources jars and pom files produced by the build with analogical artifacts
 * from last published build. If it determines that there were no changes it advises the user to
 * skip publication of the new version artifacts.
 */
public class ComparePublicationsTask extends DefaultTask {

    @Input private String projectGroup;
    @Input private String currentVersion;
    @Input private String previousVersion;
    @InputFiles private Jar sourcesJar;
    @Input private String pomTaskName;

    @InputFile private File previousVersionPomFile;
    @InputFile private File previousVersionSourcesJarFile;

    @OutputFile private File comparisonResult;

    /**
     * File that stores text result of the comparison
     */
    public File getComparisonResult() {
        return comparisonResult;
    }

    /**
     * See {@link #getComparisonResult()}
     */
    public void setComparisonResult(File comparisonResult) {
        this.comparisonResult = comparisonResult;
    }

    @TaskAction public void comparePublications() {
        new ComparePublications().comparePublications(this);
    }

    public void compareSourcesJar(Jar sourcesJar) {
        //when we compare, we can get the sources jar file via sourcesJar.archivePath
        this.sourcesJar = sourcesJar;

        //so that when we compare jars, the local sources jar is already built.
        this.dependsOn(sourcesJar);
    }

    public void comparePom(String pomTaskName) {
        this.pomTaskName = pomTaskName;

        //so that pom is created before we do comparison
        this.dependsOn(pomTaskName);
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getPreviousVersion() {
        return previousVersion;
    }

    public void setPreviousVersion(String previousVersion) {
        this.previousVersion = previousVersion;
    }

    public File getPreviousVersionPomFile() {
        return previousVersionPomFile;
    }

    public void setPreviousVersionPomFile(File previousVersionPomFile) {
        this.previousVersionPomFile = previousVersionPomFile;
    }

    public File getPreviousVersionSourcesJarFile() {
        return previousVersionSourcesJarFile;
    }

    public void setPreviousVersionSourcesJarFile(File previousVersionSourcesJarFile) {
        this.previousVersionSourcesJarFile = previousVersionSourcesJarFile;
    }

    public Jar getSourcesJar() {
        return sourcesJar;
    }

    public String getPomTaskName() {
        return pomTaskName;
    }
}

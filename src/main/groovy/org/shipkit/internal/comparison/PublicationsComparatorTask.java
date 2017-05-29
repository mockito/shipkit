package org.shipkit.internal.comparison;

import org.gradle.api.DefaultTask;
import org.gradle.api.publish.maven.tasks.GenerateMavenPom;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;
import org.shipkit.internal.util.ExposedForTesting;

import java.io.*;

/**
 * Compares sources jars and pom files produced by the build with analogical artifacts
 * from last published build. If it determines that there were no changes it advises the user to
 * skip publication of the new version artifacts.
 */
public class PublicationsComparatorTask extends DefaultTask {

    private Boolean publicationsEqual;

    private String projectGroup;
    private String currentVersion;
    private String previousVersion;
    private Jar sourcesJar;
    private String pomTaskName;

    private File previousVersionPomFile;
    private File previousVersionSourcesJarFile;

    /**
     * @return if there were any changes in builds between current and previously released version
     */
    public boolean isPublicationsEqual() {
        assert publicationsEqual != null : "Comparison task was not executed yet, the 'publicationsEqual' information not available.";
        return publicationsEqual;
    }

    @TaskAction public void comparePublications() {
        if(previousVersion == null){
            getLogger().lifecycle("{} - previousVersion is not set, nothing to compare", getPath());
            publicationsEqual = false;
            return;
        }

        GenerateMavenPom pomTask = (GenerateMavenPom) getProject().getTasks().getByName(pomTaskName);

        //TODO let's add decent validation and descriptive error messages to the user
        assert pomTask.getDestination().isFile();
        assert sourcesJar.getArchivePath().isFile();

        File currentVersionPomFile = pomTask.getDestination();
        File currentVersionSourcesJarFile = sourcesJar.getArchivePath();

        getLogger().lifecycle("{} - about to compare publications, for versions {} and {}",
                    getPath(), previousVersion, currentVersion);

        PomComparator pomComparator = new PomComparator(projectGroup, previousVersion, currentVersion);
        boolean poms = pomComparator.areEqual(previousVersionPomFile, currentVersionPomFile);
        getLogger().lifecycle("{} - pom files equal: {}", getPath(), poms);

        ZipComparator sourcesJarComparator = new ZipComparator();
        boolean jars = sourcesJarComparator.areEqual(previousVersionSourcesJarFile, currentVersionSourcesJarFile);
        getLogger().lifecycle("{} - source jars equal: {}", getPath(), jars);

        this.publicationsEqual = jars && poms;
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

    /**
     * only use for testing purposes!
     */
    @ExposedForTesting
    public void setPublicationsEqual(boolean publicationsEqual){
        this.publicationsEqual = publicationsEqual;
    }
}

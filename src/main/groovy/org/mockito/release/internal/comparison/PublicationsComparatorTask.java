package org.mockito.release.internal.comparison;

import org.gradle.api.DefaultTask;
import org.gradle.api.publish.maven.tasks.GenerateMavenPom;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;

import java.io.*;

public class PublicationsComparatorTask extends DefaultTask implements PublicationsComparator {

    private Boolean publicationsEqual;

    private String projectGroup;
    private String projectName;
    private String currentVersion;
    private String previousVersion;
    private Jar sourcesJar;
    private String pomTaskName;
    // default remote url resolver
    private RemoteUrlResolver remoteUrlResolver = new BintrayRemoteUrlResolver();
    private File tempStorageDirectory;

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

        assert pomTask.getDestination().isFile();
        assert sourcesJar.getArchivePath().isFile();

        File currentVersionPomPath = pomTask.getDestination();
        File currentVersionSourcesJarPath = sourcesJar.getArchivePath();

        getLogger().lifecycle("{} - about to compare publications, for versions {} and {}",
                    getPath(), previousVersion, currentVersion);

        tempStorageDirectory = extractTempStorageDirectoryFromPomPath(currentVersionPomPath);

        PomComparator pomComparator = new PomComparator(projectGroup, previousVersion, currentVersion);
        boolean poms = createVersionsComparator(pomComparator, ".pom", currentVersionPomPath).compare();
        getLogger().lifecycle("{} - pom files equal: {}", getPath(), poms);

        ZipComparator sourcesJarComparator = new ZipComparator();
        boolean jars = createVersionsComparator(sourcesJarComparator, "-sources.jar", currentVersionSourcesJarPath).compare();
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

    private VersionsComparator createVersionsComparator(FileComparator fileComparator, String extension, File currentVersionFile) {
        VersionsComparator comparator = new VersionsComparator();
        comparator.setFileComparator(fileComparator);
        comparator.setRemoteUrlResolver(remoteUrlResolver);
        comparator.setProjectGroup(projectGroup);
        comparator.setProjectName(projectName);
        comparator.setPreviousVersion(previousVersion);
        comparator.setExtension(extension);
        comparator.setCurrentVersionFileLocalUrl(currentVersionFile);
        comparator.setTempStorageDir(tempStorageDirectory);
        return comparator;
    }

    private File extractTempStorageDirectoryFromPomPath(File destination) {
        return destination.getParentFile();
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public RemoteUrlResolver getRemoteUrlResolver() {
        return remoteUrlResolver;
    }

    public void setRemoteUrlResolver(RemoteUrlResolver remoteUrlResolver) {
        this.remoteUrlResolver = remoteUrlResolver;
    }

}

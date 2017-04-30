package org.mockito.release.internal.comparison;

import groovy.lang.Closure;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.notes.util.IOUtil;

import java.io.*;
import java.util.Set;

public class PublicationsComparatorTask extends DefaultTask implements PublicationsComparator {

    private Boolean publicationsEqual;

    private String projectGroup;
    private String projectName;
    private String currentVersion;
    private String previousVersion;
    private String localRepository;
    private Set<BaseProjectProperties> dependentSiblingProjects;

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
        getLogger().lifecycle("{} - about to compare publications, for versions {} and {}",
                    getPath(), previousVersion, currentVersion);

        boolean poms = comparePoms();
        getLogger().lifecycle("{} - pom files equal: {}", getPath(), poms);

        boolean jars = compareJars();
        getLogger().lifecycle("{} - source jars equal: {}", getPath(), jars);

        this.publicationsEqual = jars && poms;
    }

    private boolean compareJars() {
        String extension = "-sources.jar";
        String previousSourcesRemoteUrl = getRemoteUrl(extension);
        File previousSourcesLocalUrl = getLocalArtifactUrl(previousVersion, extension);

        IOUtil.downloadToFile(previousSourcesRemoteUrl, previousSourcesLocalUrl);

        File currentSourcesLocalUrl = getLocalArtifactUrl(getCurrentVersion(), extension);

        getLogger().info("{} - compared binaries: '{}' and '{}'", getPath(), previousSourcesRemoteUrl, currentSourcesLocalUrl);

        return new ZipComparator().compareFiles(previousSourcesLocalUrl, currentSourcesLocalUrl);
    }

    private boolean comparePoms() {
        String extension = ".pom";
        String previousPomRemoteUrl = getRemoteUrl(extension);
        File previousPomLocalUrl = getLocalArtifactUrl(previousVersion, extension);

        IOUtil.downloadToFile(previousPomRemoteUrl, previousPomLocalUrl);

        File currentPomUrl = getLocalArtifactUrl(getCurrentVersion(), extension);

        String previousPomContent = IOUtil.readFully(previousPomLocalUrl);
        String currentPomContent = IOUtil.readFully(currentPomUrl);
        return new PomComparator(previousPomContent, currentPomContent, dependentSiblingProjects).areEqual();
    }

    /**
     *
     * @param extension, suffix of artifact eg ".pom" or "-sources.jar"
     * @return eg
     * https://bintray.com/shipkit/examples/download_file?file_path=/org/mockito/release-tools-example/api/0.15.1/api-0.15.1.pom";
     */
    private String getRemoteUrl(String extension){
        return "https://bintray.com/shipkit/examples/download_file?file_path="
                + getArtifactUrl(previousVersion, extension);
    }

    private File getLocalArtifactUrl(String version, String extension){
        String path = getLocalRepository() + getArtifactUrl(version, extension);
        return new File(path);
    }

    private String getArtifactUrl(String version, String extension) {
        return getProjectGroup().replace(".", "/")
                + "/" + getProjectName()
                + "/" + version
                + "/" + getProjectName()
                + "-" + version + extension;
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

    public String getLocalRepository() {
        return localRepository;
    }

    public void setLocalRepository(String localRepository) {
        this.localRepository = localRepository;
    }

    public Set<BaseProjectProperties> getDependentSiblingProjects() {
        return dependentSiblingProjects;
    }

    public void setDependentSiblingProjects(Set<BaseProjectProperties> dependentSiblingProjects) {
        this.dependentSiblingProjects = dependentSiblingProjects;
    }
}

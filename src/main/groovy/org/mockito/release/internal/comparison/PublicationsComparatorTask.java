package org.mockito.release.internal.comparison;

import groovy.lang.Closure;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.notes.util.IOUtil;

import java.io.*;

public class PublicationsComparatorTask extends DefaultTask implements PublicationsComparator {

    private final ZipComparator zipComparator = new ZipComparator(new ZipCompare());
    private Boolean publicationsEqual;

    private String projectGroup;
    private String projectName;
    private String currentVersion;
    private String previousVersion;
    private String localRepository;

    public void compareBinaries(Closure<File> left, Closure<File> right) {
        zipComparator.setPair(left, right);
    }

    public boolean isPublicationsEqual() {
        assert publicationsEqual != null : "Comparison task was not executed yet, the 'publicationsEqual' information not available.";
        return publicationsEqual;
    }

    @TaskAction public void comparePublications() {
        if(previousVersion == null){
            getLogger().lifecycle("{} - previousVersion is not set, nothing to compare", getPath());
            publicationsEqual = false;
            return;
        } else{
            getLogger().lifecycle("{} - about to compare publications, for versions {} and {}",
                    getPath(), previousVersion, currentVersion);
        }

        String previousPomRemoteUrl = getRemoteUrl(".pom");
        File previousPomLocalUrl = getLocalArtifactUrl(previousVersion, ".pom");

        IOUtil.downloadToFile(previousPomRemoteUrl, previousPomLocalUrl);

        File currentPomUrl = getLocalArtifactUrl(getCurrentVersion(), ".pom");

        String previousPomContent = IOUtil.readFully(previousPomLocalUrl);
        String currentPomContent = IOUtil.readFully(currentPomUrl);
        boolean poms = new PomComparator(previousPomContent, currentPomContent).areEqual();
        getLogger().lifecycle("{} - pom files equal: {}", getPath(), poms);

        //TODO compare artifacts too, not only poms
//        ZipComparator.Result result = zipComparator.compareFiles();
//        getLogger().info("{} - compared binaries: '{}' and '{}'", getPath(), result.getFile1(), result.getFile2());
//        boolean jars = result.areEqual();
//        getLogger().lifecycle("{} - source jars equal: {}", getPath(), jars);

        boolean jars = true;

        this.publicationsEqual = jars && poms;
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
}

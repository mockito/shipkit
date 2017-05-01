package org.mockito.release.internal.comparison;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.mockito.release.notes.util.IOUtil;

import java.io.File;

class VersionsComparator {

    private static final Logger LOG = Logging.getLogger(VersionsComparator.class);

    private FileComparator fileComparator;
    private RemoteUrlResolver remoteUrlResolver;
    private String projectGroup;
    private String projectName;
    private String previousVersion;
    private String extension;
    private File currentVersionFileLocalUrl;
    private File tempStorageDir;

    /**
     * downloads remote artifact for {@link #previousVersion}, saves it to {@link #tempStorageDir}
     * and compares it to the corresponding artifact for current version
     * @return result of comparison
     */
    public boolean compare() {
        File previousVersionFileLocalUrl = downloadRemoteFile(extension);

        LOG.info("Comparing artifacts:\n" +
                "  - {}\n" +
                "  - and {}", previousVersionFileLocalUrl, currentVersionFileLocalUrl);

        return fileComparator.areEqual(currentVersionFileLocalUrl, previousVersionFileLocalUrl);
    }

    private File downloadRemoteFile(String extension) {
        String previousFileRemoteUrl = getRemoteUrl(extension);
        File previousFileLocalUrl = getTempStorageUrl(previousVersion, extension);

        LOG.lifecycle("Downloading remote artifact\n" +
                "  - from {}\n" +
                "  - and saving it to {}", previousFileRemoteUrl, previousFileLocalUrl);

        IOUtil.downloadToFile(previousFileRemoteUrl, previousFileLocalUrl);
        return previousFileLocalUrl;
    }

    private String getRemoteUrl(String extension){
        return remoteUrlResolver.resolveUrl(projectGroup, projectName, previousVersion, extension);
    }

    private File getTempStorageUrl(String version, String extension){
        String path = tempStorageDir.getAbsolutePath()
                + File.separator + projectName + "-" + version + extension;
        return new File(path);
    }

    public FileComparator getFileComparator() {
        return fileComparator;
    }

    public void setFileComparator(FileComparator fileComparator) {
        this.fileComparator = fileComparator;
    }

    public RemoteUrlResolver getRemoteUrlResolver() {
        return remoteUrlResolver;
    }

    public void setRemoteUrlResolver(RemoteUrlResolver remoteUrlResolver) {
        this.remoteUrlResolver = remoteUrlResolver;
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

    public String getPreviousVersion() {
        return previousVersion;
    }

    public void setPreviousVersion(String previousVersion) {
        this.previousVersion = previousVersion;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public File getCurrentVersionFileLocalUrl() {
        return currentVersionFileLocalUrl;
    }

    public void setCurrentVersionFileLocalUrl(File currentVersionFileLocalUrl) {
        this.currentVersionFileLocalUrl = currentVersionFileLocalUrl;
    }

    public File getTempStorageDir() {
        return tempStorageDir;
    }

    public void setTempStorageDir(File tempStorageDir) {
        this.tempStorageDir = tempStorageDir;
    }
}

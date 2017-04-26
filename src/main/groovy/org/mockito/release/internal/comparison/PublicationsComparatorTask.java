package org.mockito.release.internal.comparison;

import groovy.lang.Closure;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.notes.util.IOUtil;

import java.io.*;
import java.net.URL;

public class PublicationsComparatorTask extends DefaultTask implements PublicationsComparator {

    private final ZipComparator zipComparator = new ZipComparator(new ZipCompare());
    private final PomComparator pomComparator = new PomComparator();
    private Boolean publicationsEqual;

    private String projectGroup;
    private String projectName;
    private String currentVersion;
    private String previousVersion;
    private String localRepository;

    public void compareBinaries(Closure<File> left, Closure<File> right) {
        zipComparator.setPair(left, right);
    }

    public void comparePoms(Closure<String> left, Closure<String> right) {
        pomComparator.setPair(left, right);
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

        final String previousUrl = downloadArtifact(".pom");
        final String currentUrl = getLocalArtifactUrl(getCurrentVersion(), ".pom");

        pomComparator.setPair(
            new Closure<String>(null) {
                @Override
                public String call() {
                    String content = IOUtil.readFully(new File(previousUrl));
                    return content;
                }
            },
            new Closure<String>(null) {
                @Override
                public String call() {
                    String content = IOUtil.readFully(new File(currentUrl));
                    return content;
                }
            });
        boolean poms = pomComparator.areEqual();
        getLogger().lifecycle("{} - pom files equal: {}", getPath(), poms);

        //TODO compare artifacts too, not only poms
//        ZipComparator.Result result = zipComparator.compareFiles();
//        getLogger().info("{} - compared binaries: '{}' and '{}'", getPath(), result.getFile1(), result.getFile2());
//        boolean jars = result.areEqual();
//        getLogger().lifecycle("{} - source jars equal: {}", getPath(), jars);

        boolean jars = true;

        this.publicationsEqual = jars && poms;
    }

    private String downloadArtifact(String extension){
        InputStream input = null;
        FileOutputStream output = null;
        try {
            String url = getRemoteUrl(extension);
            input = new BufferedInputStream(new URL(url).openStream());
            String localUrl = getLocalArtifactUrl(getPreviousVersion(), extension);

            IOUtil.createParentDirectory(new File(localUrl));


            FileOutputStream fos = new FileOutputStream(localUrl);
            byte[] buf = new byte[1024];
            int n;
            while ((n=input.read(buf)) != -1) {
                fos.write(buf, 0, n);
            }
            return localUrl;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally{
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        }
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

    private String getLocalArtifactUrl(String version, String extension){
        return getLocalRepository() + getArtifactUrl(version, extension);
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

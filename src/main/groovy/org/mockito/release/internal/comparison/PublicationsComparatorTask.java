package org.mockito.release.internal.comparison;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.publish.maven.tasks.GenerateMavenPom;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;
import org.mockito.release.internal.comparison.artifact.DefaultArtifactUrlResolver;
import org.mockito.release.internal.util.ExposedForTesting;

import java.io.*;

/**
 * Compares sources jars and pom files produced by the build with analogical artifacts
 * from last published build. If it determines that there were no changes it advises the user to
 * skip publication of the new version artifacts.
 */
public class PublicationsComparatorTask extends DefaultTask {

    private Boolean publicationsEqual;

    private String projectGroup;
    private String artifactName;
    private String currentVersion;
    private String previousVersion;
    private Jar sourcesJar;
    private String pomTaskName;

    private String previousVersionPomUrl;
    private String previousVersionSourcesJarUrl;

    private DefaultArtifactUrlResolver defaultArtifactUrlResolver;

    private File tempStorageDirectory;

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

        File currentVersionPomPath = pomTask.getDestination();
        File currentVersionSourcesJarPath = sourcesJar.getArchivePath();

        previousVersionPomUrl = getDefaultIfNull(previousVersionPomUrl, "previousVersionPomUrl", ".pom");
        previousVersionSourcesJarUrl = getDefaultIfNull(previousVersionSourcesJarUrl, "previousSourcesJarUrl", "-sources.jar");

        artifactName = sourcesJar.getBaseName();

        getLogger().lifecycle("{} - about to compare publications, for versions {} and {}",
                    getPath(), previousVersion, currentVersion);

        tempStorageDirectory = extractTempStorageDirectoryFromPomPath(currentVersionPomPath);

        PomComparator pomComparator = new PomComparator(projectGroup, previousVersion, currentVersion);
        boolean poms = createVersionsComparator(pomComparator, ".pom", currentVersionPomPath, previousVersionPomUrl).compare();
        getLogger().lifecycle("{} - pom files equal: {}", getPath(), poms);

        ZipComparator sourcesJarComparator = new ZipComparator();
        boolean jars = createVersionsComparator(sourcesJarComparator, "-sources.jar", currentVersionSourcesJarPath, previousVersionSourcesJarUrl).compare();
        getLogger().lifecycle("{} - source jars equal: {}", getPath(), jars);

        this.publicationsEqual = jars && poms;
    }

    private String getDefaultIfNull(String url, String variableName, String extension) {
        if(url == null){
            if(defaultArtifactUrlResolver == null){
                throw new GradleException("You have to configure " + variableName + " to use PublicationsComparatorTask.\n"
                        + "If you use one of the supported publishing plugins default url will be configured for you.\n"
                        + "Currently supported plugins: Bintray"
                );
            }
            String defaultUrl = defaultArtifactUrlResolver.getDefaultUrl(extension);
            getLogger().lifecycle("Variable {} not set. Setting it to default value - {}", variableName, defaultUrl);
            return defaultUrl;
        }
        return url;
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

    private VersionsComparator createVersionsComparator(FileComparator fileComparator, String extension, File currentVersionFile, String previousVersionFileUrl) {
        VersionsComparator comparator = new VersionsComparator();
        comparator.setFileComparator(fileComparator);
        comparator.setPreviousVersionFileRemoteUrl(previousVersionFileUrl);
        comparator.setProjectGroup(projectGroup);
        comparator.setProjectName(artifactName);
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

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
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

    public String getPreviousVersionPomUrl() {
        return previousVersionPomUrl;
    }

    public void setPreviousVersionPomUrl(String previousVersionPomUrl) {
        this.previousVersionPomUrl = previousVersionPomUrl;
    }

    public String getPreviousVersionSourcesJarUrl() {
        return previousVersionSourcesJarUrl;
    }

    public void setPreviousVersionSourcesJarUrl(String previousVersionSourcesJarUrl) {
        this.previousVersionSourcesJarUrl = previousVersionSourcesJarUrl;
    }

    public DefaultArtifactUrlResolver getDefaultArtifactUrlResolver() {
        return defaultArtifactUrlResolver;
    }

    public void setDefaultArtifactUrlResolver(DefaultArtifactUrlResolver defaultArtifactUrlResolver) {
        this.defaultArtifactUrlResolver = defaultArtifactUrlResolver;
    }

    /**
     * only use for test purposes!
     */
    @ExposedForTesting
    public void setPublicationsEqual(boolean publicationsEqual){
        this.publicationsEqual = publicationsEqual;
    }
}

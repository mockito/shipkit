package org.shipkit.gradle.java;

import org.gradle.api.DefaultTask;
import org.gradle.api.publish.maven.tasks.GenerateMavenPom;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;
import org.shipkit.internal.gradle.java.tasks.ComparePublications;

import java.io.File;

/**
 * Compares sources jars and pom files produced by the build with analogical artifacts
 * from last published build. If it determines that there were no changes it advises the user to
 * skip publication of the new version artifacts (e.g. skip the release).
 * <p>
 * The outputs of this task are used by {@link org.shipkit.gradle.ReleaseNeededTask}.
 * The {@link #getComparisonResult()} should be added to {@link org.shipkit.gradle.ReleaseNeededTask#addComparisonResult(File)}.
 */
public class ComparePublicationsTask extends DefaultTask {

    @Input private String projectGroup;
    @Input private String currentVersion;
    @Input @Optional private String previousVersion;
    @InputFiles private Jar sourcesJar;
    @Input private String pomTaskName;

    @InputFile private File previousPom;
    @InputFile private File previousSourcesJar;

    @OutputFile private File comparisonResult;

    /**
     * File that stores text result of the comparison.
     * If the file is empty it means the publications are the same.
     * If the file does not exist it means that the task did not run.
     * In this case you can assume that there are differences and the release should be triggered.
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

    /**
     * Sets the sourcesJar for comparision with {@link #getPreviousSourcesJar()}.
     * Task dependency will be automatically added from this task to sourcesJar task supplied as parameter.
     * During comparison, the algorithm will read jar's output file using {@link Jar#getArchivePath()}.
     */
    public void compareSourcesJar(Jar sourcesJar) {
        //when we compare, we can get the sources jar file via sourcesJar.archivePath
        this.sourcesJar = sourcesJar;

        //so that when we compare jars, the local sources jar is already built.
        this.dependsOn(sourcesJar);
    }

    /**
     * Sets the pom task name for comparision with {@link #getPreviousSourcesJar()}.
     * Task dependency will be automatically added from this task to pomTaskName supplied as parameter.
     * During comparison, the algorithm will get the pom task, cast it to {@link GenerateMavenPom},
     * and read {@link GenerateMavenPom#getDestination()}.
     */
    public void comparePom(String pomTaskName) {
        this.pomTaskName = pomTaskName;

        //so that pom is created before we do comparison
        this.dependsOn(pomTaskName);
    }

    /**
     * The artifact group, used during pom comparison to ignore version changes of artifacts with that group.
     * Needed to make the pom comparison robust and avoid false positives.
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
     * Current project version, used during pom comparison to ignore version changes of internal dependencies.
     * Used to make pom comparison robust and avoid false positives.
     */
    public String getCurrentVersion() {
        return currentVersion;
    }

    /**
     * See {@link #getCurrentVersion()}
     */
    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    /**
     * Previous version, used during pom comparison to ignore version changes of internal dependencies.
     * Used to make pom comparison robust and avoid false positives.
     * <p>
     * If null it means there was no previous version and the task will not run any diffs.
     */
    public String getPreviousVersion() {
        return previousVersion;
    }

    /**
     * See {@link #getPreviousVersion()}
     */
    public void setPreviousVersion(String previousVersion) {
        this.previousVersion = previousVersion;
    }

    /**
     * Previously released pom file used for comparison with currently built pom file.
     */
    public File getPreviousPom() {
        return previousPom;
    }

    /**
     * See {@link #getPreviousPom()}
     */
    public void setPreviousPom(File previousPom) {
        this.previousPom = previousPom;
    }

    /**
     * Previously released sources jar used for comparison with currently built sources jar.
     */
    public File getPreviousSourcesJar() {
        return previousSourcesJar;
    }

    /**
     * See {@link #getPreviousSourcesJar()}
     */
    public void setPreviousSourcesJar(File previousSourcesJar) {
        this.previousSourcesJar = previousSourcesJar;
    }

    /**
     * Currently built sources jar file used for comparison.
     */
    public Jar getSourcesJar() {
        return sourcesJar;
    }

    /**
     * Pom task name that builds current pom file.
     * The task must be of type {@link GenerateMavenPom}.
     */
    public String getPomTaskName() {
        return pomTaskName;
    }
}

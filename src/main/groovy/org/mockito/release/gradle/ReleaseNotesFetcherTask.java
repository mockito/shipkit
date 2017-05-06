package org.mockito.release.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.internal.gradle.util.ReleaseNotesSerializer;
import org.mockito.release.notes.generator.ReleaseNotesGenerator;
import org.mockito.release.notes.generator.ReleaseNotesGenerators;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;

/**
 * Fetches release notes data information from Git and GitHub and serializes it to {@link #outputFile}.
 */
public class ReleaseNotesFetcherTask extends DefaultTask {

    @Input private String previousVersion;
    @Input private String gitHubReadOnlyAuthToken;
    @Input private String gitHubRepository;
    @OutputFile private File outputFile;

    /**
     * GitHub read only authentication token needed for loading issue information from GitHub.
     */
    public String getGitHubReadOnlyAuthToken() {
        return gitHubReadOnlyAuthToken;
    }

    /**
     * See {@link #getGitHubReadOnlyAuthToken()}
     */
    public void setGitHubReadOnlyAuthToken(String readOnlyToken) {
        this.gitHubReadOnlyAuthToken = readOnlyToken;
    }

    /**
     * Name of the GitHub repository in format "user|org/repository",
     * for example: "mockito/mockito"
     */
    public String getGitHubRepository() {
        return gitHubRepository;
    }

    /**
     * See {@link #getGitHubRepository()}
     */
    public void setGitHubRepository(String gitHubRepository) {
        this.gitHubRepository = gitHubRepository;
    }

    /**
     * Previous released version we generate the release notes from.
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
     * The file release notes data will be saved to
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

    @TaskAction
    public void generateReleaseNotes() {
        ReleaseNotesGenerator generator = ReleaseNotesGenerators.releaseNotesGenerator(
                getProject().getRootDir(), getGitHubRepository(), getGitHubReadOnlyAuthToken());

        //TODO expose other values hardcoded here as class fields/properties, e.g.
        // target version, tag prefix, only pull requests
        Collection<ReleaseNotesData> releaseNotes = generator.generateReleaseNotesData(
                getProject().getVersion().toString(), asList(getPreviousVersion()), "v",
                Collections.<String>emptyList(), false);

        ReleaseNotesSerializer releaseNotesSerializer = new ReleaseNotesSerializer(getOutputFile());
        releaseNotesSerializer.serialize(releaseNotes);
    }
}

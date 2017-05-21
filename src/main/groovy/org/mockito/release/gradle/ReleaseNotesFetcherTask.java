package org.mockito.release.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.internal.gradle.util.ReleaseNotesSerializer;
import org.mockito.release.notes.generator.ReleaseNotesGenerator;
import org.mockito.release.notes.generator.ReleaseNotesGenerators;
import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.notes.util.IOUtil;
import org.mockito.release.notes.vcs.DefaultCommitApprover;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;

/**
 * Fetches release notes data information from Git and GitHub and serializes it to {@link #outputFile}.
 */
public class ReleaseNotesFetcherTask extends DefaultTask {

    @Input private String previousVersion;
    @Input private String version = getProject().getVersion().toString();
    @Input private String gitHubReadOnlyAuthToken;
    @Input private String gitHubRepository;
    @Input private String tagPrefix = "v";
    @Input private boolean onlyPullRequests;
    @Input private File gitWorkDir = getProject().getRootDir();
    @Input private Collection<String> gitHubLabels = Collections.emptyList();
    @Input private String skipCommitMessagePostfix;
    @OutputFile private File outputFile;

    /**
     * See {@link ReleaseConfiguration.GitHub#getReadOnlyAuthToken()}
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
     * See {@link ReleaseConfiguration.GitHub#getRepository()}
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
     * See {@link ReleaseConfiguration#getPreviousReleaseVersion()}
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

    /**
     * Version we generate release notes data for
     */
    public String getVersion() {
        return version;
    }

    /**
     * See {@link #getVersion()}
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Whether to include only pull requests in the release notes data
     */
    public boolean isOnlyPullRequests() {
        return onlyPullRequests;
    }

    /**
     * See {@link #isOnlyPullRequests()}
     */
    public void setOnlyPullRequests(boolean onlyPullRequests) {
        this.onlyPullRequests = onlyPullRequests;
    }

    /**
     * See {@link ReleaseConfiguration.Git#getTagPrefix()}
     */
    public String getTagPrefix() {
        return tagPrefix;
    }

    /**
     * See {@link #getTagPrefix()}
     */
    public void setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    /**
     * Work directory where git operations will be invoked (like 'git log', etc.)
     */
    public File getGitWorkDir() {
        return gitWorkDir;
    }

    /**
     * See {@link #getGitWorkDir()}
     */
    public void setGitWorkDir(File gitWorkDir) {
        this.gitWorkDir = gitWorkDir;
    }

    /**
     * GitHub labels to include when querying GitHub issues API.
     * If empty, then all labels will be included.
     * If labels are configured, only tickets with those labels will be included in the release notes.
     */
    public Collection<String> getGitHubLabels() {
        return gitHubLabels;
    }

    /**
     * See {@link #getGitHubLabels()}
     */
    public void setGitHubLabels(Collection<String> gitHubLabels) {
        this.gitHubLabels = gitHubLabels;
    }

    /**
     * Configurable commit message postfix that will cause with commit skipping in release notes
     * If empty, only default [ci skip] will be used
     */
    public String getSkipCommitMessagePostfix() {
        return this.skipCommitMessagePostfix;
    }

    /**
     * See {@link #getSkipCommitMessagePostfix()}
     */
    public void setSkipCommitMessagePostfix(String skipCommitMessagePostfix) {
        /*

        TODO mk - can you convert this to a list of ignored commit substrings?

        I think the original design I put together has flaws.
        commitMessagePostfix is dynamic and changes with every build because it contains Travis build number.
        Therefore we cannot really depend on it to exclude commits reliably.

        I suggest that we introduce a new setting to configure how to exclude commits from release generation.
        This will make it explicit and easy to tweak by the user.
        Suggested plan

        1. New setting: "releasing.releaseNotes.ignoreCommitsContaining", Collection<String>
        2. By default, we would configure it to: "ignoreCommitsContaining" = ['[ci skip]']
        3. Users can tweak it. For example, in Mockito project, in "release.gradle" we would do:
            releasing.releaseNotes.ignoreCommitsContaining = ['[ci skip]', '[ci skip-release]']

         */
        this.skipCommitMessagePostfix = skipCommitMessagePostfix;
    }

    @TaskAction
    public void generateReleaseNotes() {
        ReleaseNotesGenerator generator = ReleaseNotesGenerators.releaseNotesGenerator(
                gitWorkDir, gitHubRepository, gitHubReadOnlyAuthToken, new DefaultCommitApprover(skipCommitMessagePostfix));

        Collection<ReleaseNotesData> releaseNotes = generator.generateReleaseNotesData(
                version, asList(previousVersion), tagPrefix, gitHubLabels, onlyPullRequests);

        ReleaseNotesSerializer releaseNotesSerializer = new ReleaseNotesSerializer();
        final String serializedData = releaseNotesSerializer.serialize(releaseNotes);
        IOUtil.writeFile(getOutputFile(), serializedData);
    }
}

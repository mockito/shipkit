package org.shipkit.internal.gradle;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;

import java.io.File;
import java.util.Collection;

//TODO expose as public API
public class NotesGeneration {
    private File gitWorkingDir;
    private String gitHubApiUrl;
    private String gitHubRepository;
    private String gitHubReadOnlyAuthToken;
    private Collection<String> targetVersions;
    private String tagPrefix;
    private Collection<String> gitHubLabels;
    private File outputFile;
    private boolean onlyPullRequests;
    private String introductionText;
    private String detailedReleaseNotesLink;
    private String vcsCommitsLinkTemplate;
    private String headVersion;
    private File temporarySerializedNotesFile;
    private String skipCommitMessagePostfix;
    private Collection<String> ignoreCommitsContaining;

    public File getGitWorkingDir() {
        return gitWorkingDir;
    }

    public void setGitWorkingDir(File gitWorkingDir) {
        this.gitWorkingDir = gitWorkingDir;
    }

    public String getGitHubApiUrl() {
        return gitHubApiUrl;
    }

    public void setGitHubApiUrl(String gitHubApiUrl) {
        this.gitHubApiUrl = gitHubApiUrl;
    }

    public String getGitHubRepository() {
        return gitHubRepository;
    }

    public void setGitHubRepository(String gitHubRepository) {
        this.gitHubRepository = gitHubRepository;
    }

    public String getGitHubReadOnlyAuthToken() {
        return gitHubReadOnlyAuthToken;
    }

    public void setGitHubReadOnlyAuthToken(String gitHubReadOnlyAuthToken) {
        this.gitHubReadOnlyAuthToken = gitHubReadOnlyAuthToken;
    }

    @Input
    public Collection<String> getTargetVersions() {
        return targetVersions;
    }

    public void setTargetVersions(Collection<String> targetVersions) {
        this.targetVersions = targetVersions;
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public void setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    public Collection<String> getGitHubLabels() {
        return gitHubLabels;
    }

    public void setGitHubLabels(Collection<String> gitHubLabels) {
        this.gitHubLabels = gitHubLabels;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public boolean isOnlyPullRequests() {
        return onlyPullRequests;
    }

    public void setOnlyPullRequests(boolean onlyPullRequests) {
        this.onlyPullRequests = onlyPullRequests;
    }

    public String getIntroductionText() {
        return introductionText;
    }

    public void setIntroductionText(String introductionText) {
        this.introductionText = introductionText;
    }

    public String getDetailedReleaseNotesLink() {
        return detailedReleaseNotesLink;
    }

    public void setDetailedReleaseNotesLink(String detailedReleaseNotesLink) {
        this.detailedReleaseNotesLink = detailedReleaseNotesLink;
    }

    public String getVcsCommitsLinkTemplate() {
        return vcsCommitsLinkTemplate;
    }

    public void setVcsCommitsLinkTemplate(String vcsCommitsLinkTemplate) {
        this.vcsCommitsLinkTemplate = vcsCommitsLinkTemplate;
    }

    public String getHeadVersion() {
        return headVersion;
    }

    public void setHeadVersion(String headVersion) {
        this.headVersion = headVersion;
    }

    @OutputFile
    public File getTemporarySerializedNotesFile() {
        return temporarySerializedNotesFile;
    }

    public void setTemporarySerializedNotesFile(File temporarySerializedNotesFile) {
        this.temporarySerializedNotesFile = temporarySerializedNotesFile;
    }

    public String getSkipCommitMessagePostfix() {
        return skipCommitMessagePostfix;
    }

    public void setSkipCommitMessagePostfix(String skipCommitMessagePostfix) {
        this.skipCommitMessagePostfix = skipCommitMessagePostfix;
    }

    public Collection<String> getIgnoreCommitsContaining() {
        return ignoreCommitsContaining;
    }

    public void setIgnoreCommitsContaining(Collection<String> ignoreCommitsContaining) {
        this.ignoreCommitsContaining = ignoreCommitsContaining;
    }
}

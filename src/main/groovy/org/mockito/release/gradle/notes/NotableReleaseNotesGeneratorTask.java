package org.mockito.release.gradle.notes;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.notes.format.ReleaseNotesFormatters;
import org.mockito.release.notes.generator.ReleaseNotesGenerator;
import org.mockito.release.notes.generator.ReleaseNotesGenerators;
import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.notes.util.IOUtil;

import java.io.File;
import java.util.Collection;

public class NotableReleaseNotesGeneratorTask extends DefaultTask {

    private File gitWorkingDir;
    private String gitHubAuthToken;
    private Collection<String> targetVersions;
    private String tagPrefix;
    private Collection<String> gitHubLabels;
    private File outputFile;
    private boolean onlyPullRequests;
    private String introductionText;
    private String detailedReleaseNotesLink;
    private String vcsCommitsLinkTemplate;

    @TaskAction public void generateReleaseNotes() {

        //TODO SF this task is not functioning, I'm using it only to model the public API of interfaces I need.

        ReleaseNotesGenerator generator = ReleaseNotesGenerators.releaseNotesGenerator(gitWorkingDir, gitHubAuthToken);
        Collection<ReleaseNotesData> releaseNotes = generator.generateReleaseNotesData(targetVersions, tagPrefix, gitHubLabels, onlyPullRequests);
        String notes = ReleaseNotesFormatters.notableFormatter(introductionText, detailedReleaseNotesLink, vcsCommitsLinkTemplate).formatReleaseNotes(releaseNotes);
        IOUtil.writeFile(outputFile, notes);
    }
}

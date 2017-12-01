package org.shipkit.internal.gradle.notes.tasks;

import org.gradle.api.GradleException;
import org.shipkit.gradle.notes.FetchReleaseNotesTask;
import org.shipkit.internal.gradle.util.ReleaseNotesSerializer;
import org.shipkit.internal.notes.generator.ReleaseNotesGenerator;
import org.shipkit.internal.notes.generator.ReleaseNotesGenerators;
import org.shipkit.internal.notes.model.ReleaseNotesData;
import org.shipkit.internal.notes.util.IOUtil;
import org.shipkit.internal.notes.vcs.IgnoredCommit;
import org.shipkit.internal.notes.vcs.RevisionNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;

public class FetchReleaseNotes {
    public void fetchReleaseNotes(FetchReleaseNotesTask task) {
        try {
            performFetchReleaseNotes(task);
        } catch (RevisionNotFoundException e) {
            String message = buildUnknownRevisionMessage(e);
            throw new GradleException(message);
        }
    }

    private void performFetchReleaseNotes(FetchReleaseNotesTask task) throws RevisionNotFoundException {
        ReleaseNotesGenerator generator = ReleaseNotesGenerators.releaseNotesGenerator(
            task.getGitWorkDir(), task.getGitHubApiUrl(), task.getGitHubRepository(),
            task.getGitHubReadOnlyAuthToken(), new IgnoredCommit(task.getIgnoreCommitsContaining()));

        List<String> targetVersions = task.getPreviousVersion() == null ? new ArrayList<String>() : singletonList(task.getPreviousVersion());
        Collection<ReleaseNotesData> releaseNotes = generator.generateReleaseNotesData(
            task.getVersion(), targetVersions, task.getTagPrefix(), task.getGitHubLabels(), task.isOnlyPullRequests());

        ReleaseNotesSerializer releaseNotesSerializer = new ReleaseNotesSerializer();
        final String serializedData = releaseNotesSerializer.serialize(releaseNotes);
        IOUtil.writeFile(task.getOutputFile(), serializedData);
    }

    private String buildUnknownRevisionMessage(RevisionNotFoundException exception) {
        return "Version " + exception.getRevision() + " has not been found in VCS. Probably there is no" +
            " corresponding tag in VCS for \"previousVersion\" from version.properties file.";
    }
}

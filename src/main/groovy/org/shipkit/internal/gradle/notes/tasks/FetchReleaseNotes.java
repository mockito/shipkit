package org.shipkit.internal.gradle.notes.tasks;

import org.shipkit.gradle.notes.FetchReleaseNotesTask;
import org.shipkit.internal.gradle.util.ReleaseNotesSerializer;
import org.shipkit.internal.notes.generator.ReleaseNotesGenerator;
import org.shipkit.internal.notes.generator.ReleaseNotesGenerators;
import org.shipkit.internal.notes.model.ReleaseNotesData;
import org.shipkit.internal.notes.util.IOUtil;
import org.shipkit.internal.notes.vcs.IgnoredCommit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;

public class FetchReleaseNotes {
    public void fetchReleaseNotes(FetchReleaseNotesTask task) {
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
}

package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.internal.gradle.util.ReleaseNotesSerializer;
import org.mockito.release.notes.generator.ReleaseNotesGenerator;
import org.mockito.release.notes.generator.ReleaseNotesGenerators;
import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.notes.util.IOUtil;
import org.mockito.release.notes.vcs.DefaultCommitApprover;

import java.util.Collection;

//TODO document and elevate to public API
public class NotableReleaseNotesFetcherTask extends DefaultTask {

    private final NotesGeneration notesGeneration = new NotesGeneration();

    @Nested
    public NotesGeneration getNotesGeneration() {
        return notesGeneration;
    }

    @TaskAction
    public void generateReleaseNotes() {
        ReleaseNotesGenerator generator = ReleaseNotesGenerators.releaseNotesGenerator(
                notesGeneration.getGitWorkingDir(), notesGeneration.getGitHubRepository(), notesGeneration.getGitHubReadOnlyAuthToken(), new DefaultCommitApprover());
        //TODO release notes generation should produce JSON data that we can keep between the builds in the cache
        //then, the markdown generation logic would parse the JSON and produce human readable notes
        Collection<ReleaseNotesData> releaseNotes = generator.generateReleaseNotesData(
                notesGeneration.getHeadVersion(), notesGeneration.getTargetVersions(), notesGeneration.getTagPrefix(), notesGeneration.getGitHubLabels(), notesGeneration.isOnlyPullRequests());

        ReleaseNotesSerializer releaseNotesSerializer = new ReleaseNotesSerializer();
        final String serializedData = releaseNotesSerializer.serialize(releaseNotes);
        IOUtil.writeFile(notesGeneration.getTemporarySerializedNotesFile(), serializedData);
    }
}

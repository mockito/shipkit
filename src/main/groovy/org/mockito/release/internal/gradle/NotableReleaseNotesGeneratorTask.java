package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.internal.gradle.util.ReleaseNotesSerializer;
import org.mockito.release.notes.format.ReleaseNotesFormatters;
import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.notes.util.IOUtil;

import java.io.*;
import java.util.Collection;

public class NotableReleaseNotesGeneratorTask extends DefaultTask {

    //TODO documentation
    private final NotesGeneration notesGeneration = new NotesGeneration();

    public NotesGeneration getNotesGeneration() {
        return notesGeneration;
    }

    @TaskAction public void generateReleaseNotes() {
        ReleaseNotesSerializer releaseNotesSerializer = new ReleaseNotesSerializer(notesGeneration.getTemporarySerializedNotesFile());
        Collection<ReleaseNotesData> releaseNotes = releaseNotesSerializer.deserialize();
        String notes = ReleaseNotesFormatters.notableFormatter(
                notesGeneration.getIntroductionText(), notesGeneration.getDetailedReleaseNotesLink(), notesGeneration.getVcsCommitsLinkTemplate())
                .formatReleaseNotes(releaseNotes);
        IOUtil.writeFile(notesGeneration.getOutputFile(), notes);
    }
}

package org.shipkit.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.util.ReleaseNotesSerializer;
import org.shipkit.internal.notes.format.ReleaseNotesFormatters;
import org.shipkit.internal.notes.model.ReleaseNotesData;
import org.shipkit.internal.notes.util.IOUtil;

import java.util.Collection;

//TODO document and elevate to public API
public class NotableReleaseNotesGeneratorTask extends DefaultTask {

    private final NotesGeneration notesGeneration = new NotesGeneration();

    public NotesGeneration getNotesGeneration() {
        return notesGeneration;
    }

    @TaskAction public void generateReleaseNotes() {
        ReleaseNotesSerializer releaseNotesSerializer = new ReleaseNotesSerializer();
        final String serializedNotesData = IOUtil.readFully(notesGeneration.getTemporarySerializedNotesFile());
        Collection<ReleaseNotesData> releaseNotes = releaseNotesSerializer.deserialize(serializedNotesData);
        String notes = ReleaseNotesFormatters.notableFormatter(
                notesGeneration.getIntroductionText(), notesGeneration.getDetailedReleaseNotesLink(), notesGeneration.getVcsCommitsLinkTemplate())
                .formatReleaseNotes(releaseNotes);
        IOUtil.writeFile(notesGeneration.getOutputFile(), notes);
    }
}

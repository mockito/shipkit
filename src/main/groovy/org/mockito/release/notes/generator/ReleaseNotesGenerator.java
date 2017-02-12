package org.mockito.release.notes.generator;

import org.mockito.release.notes.model.ReleaseNotesData;

import java.util.Collection;

public interface ReleaseNotesGenerator {

    Collection<ReleaseNotesData> generateReleaseNotes(ReleaseNotesParameters parameters);
}

package org.mockito.release.internal.gradle.util;

import org.mockito.release.notes.model.ReleaseNotesData;

import java.util.Collection;

public interface ReleaseNotesSerializer {
    void serialize(Collection<ReleaseNotesData> releaseNotes);
    Collection<ReleaseNotesData> deserialize();
}

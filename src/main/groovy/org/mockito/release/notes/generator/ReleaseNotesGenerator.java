package org.mockito.release.notes.generator;

import org.mockito.release.notes.model.VersionNotesData;

import java.util.Collection;

public interface ReleaseNotesGenerator {

    Collection<VersionNotesData> generateReleaseNotes(String startVersion, Collection<String> targetVersions,
                                                      String tagPrefix, Collection<String> gitHubLabels);
}
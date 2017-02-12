package org.mockito.release.notes.generator;

import org.mockito.release.notes.model.ReleaseNotesData;

import java.util.Collection;

/**
 * Generates release notes data model. The model can be used to generate human-readable release notes text.
 */
public interface ReleaseNotesGenerator {

    Collection<ReleaseNotesData> generateReleaseNotes(String startVersion, Collection<String> targetVersions,
                                                      String tagPrefix, Collection<String> gitHubLabels);
}
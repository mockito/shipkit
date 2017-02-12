package org.mockito.release.notes.generator;

import org.mockito.release.notes.model.ReleaseNotesData;

import java.util.Collection;

/**
 * Generates release notes data model. The model can be used to generate human-readable release notes text.
 */
public interface ReleaseNotesGenerator {

    /**
     * Generates release notes data model.
     * @param targetVersions target versions, _important_:
     *                       1) must be ordered newest first,
     *                       2) last version will _not_ be included in resulting model,
     *                       it is only used to generate delta notes between last version and second-last version.
     *                       Example: for "1.3", "1.2", "1.1", only "1.3" and "1.2" will be included in the result.
     * @param tagPrefix tag prefix added to version so that it becomes vcs addressable revision (tag).
     *                  Typically it is "v". Empty string is ok, it means that there is no prefix.
     * @param gitHubLabels only include improvements with one of those labels.
 *                     The report should be concise so please include small set of labels.
     */
    Collection<ReleaseNotesData> generateReleaseNotes(Collection<String> targetVersions,
                                                      String tagPrefix, Collection<String> gitHubLabels);
}
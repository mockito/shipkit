package org.shipkit.internal.notes.generator;

import org.shipkit.internal.notes.model.ReleaseNotesData;
import org.shipkit.internal.notes.vcs.RevisionNotFoundException;

import java.util.Collection;

/**
 * Generates release notes data model. The model can be used to generate human-readable release notes text.
 */
public interface ReleaseNotesGenerator {

    /**
     * Generates release notes data model.
     * @param headVersion optional (nullable)
     *                    if provided the first release notes data on the result will be the head version
     *                   (data generated from HEAD)
     * @param targetVersions target versions, _important_:
     *                       1) must be ordered newest first,
     *                       2) last version will _not_ be included in resulting model,
     *                       it is only used to generate delta notes between last version and second-last version.
     *                       Example: for "1.3", "1.2", "1.1", only "1.3" and "1.2" will be included in the result.
     * @param tagPrefix tag prefix added to version so that it becomes vcs addressable revision (tag).
     *                  Typically it is "v". Empty string is ok, it means that there is no prefix.
     * @param gitHubLabels only include improvements with one of those labels.
     *                     The report should be concise so please include small set of labels.
     *                     If no labels are provided, _all_ improvements are included!
     * @param onlyPullRequests only include pull requests in the data
     */
    Collection<ReleaseNotesData> generateReleaseNotesData(String headVersion, Collection<String> targetVersions,
                                                          String tagPrefix, Collection<String> gitHubLabels,
                                                          boolean onlyPullRequests) throws RevisionNotFoundException;
}

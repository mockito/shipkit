package org.shipkit.internal.notes.vcs;

import java.util.Collection;
import java.util.Date;

/**
 * Provides release dates for versions
 */
public interface ReleasedVersionsProvider {

    /**
     * Provides release versions information, with dates for given versions.
     *
     * @param headVersion optional (nullable), will be the first released version in the returned list
     * @param headDate optional (nullable), required only when head version is provided
     * @param versions required, for example: 1.2.0, 1.1.0, 1.0.0, must be ordered descending!
     *                 If head version is not provided, at least 2 versions are needed.
     *                 If head version is provided, at least one version is needed.
     * @param tagPrefix required (at least empty String)
     *                  tag prefix, adding it to the version String should create vcs addressable revision, tag.
     */
    Collection<ReleasedVersion> getReleasedVersions(String headVersion, Date headDate, Collection<String> versions, String tagPrefix) throws RevisionNotFoundException;
}

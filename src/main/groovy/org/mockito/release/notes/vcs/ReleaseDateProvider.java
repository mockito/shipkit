package org.mockito.release.notes.vcs;

import java.util.Date;
import java.util.Map;

/**
 * Provides release dates for versions
 */
public interface ReleaseDateProvider {

    /**
     * Provides release dates for given versions. Versions should be ordered newer first.
     * Last version is not included in the result. It is only used to calculate version range when getting data from Git.
     *
     * @param versions for example: 1.2.0, 1.1.0, 1.0.0. Last version is not included in result.
     * @param tagPrefix optional tag prefix, adding it to the version String should create vcs addressable revision, tag.
     *                  Typically it is "v" or empty String if no tag prefix is used.
     */
    Map<String, Date> getReleaseDates(Iterable<String> versions, String tagPrefix);
}

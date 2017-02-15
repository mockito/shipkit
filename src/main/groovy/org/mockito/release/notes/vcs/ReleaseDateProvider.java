package org.mockito.release.notes.vcs;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Provides release dates for versions
 */
public interface ReleaseDateProvider {

    /**
     * Provides release dates for given versions.
     *
     * @param versions for example: 1.2.0, 1.1.0, 1.0.0
     * @param tagPrefix optional tag prefix, adding it to the version String should create vcs addressable revision, tag.
     *                  Typically it is "v" or empty String if no tag prefix is used.
     */
    Map<String, Date> getReleaseDates(Collection<String> versions, String tagPrefix);
}

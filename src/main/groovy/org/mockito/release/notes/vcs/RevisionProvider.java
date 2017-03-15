package org.mockito.release.notes.vcs;

/**
 * Provides revision numbers for changes
 */
public interface RevisionProvider {

    /**
     * Convert tag (or revision) to revision number
     */
    String getRevisionForTagOrRevision(String tagOrRevision);
}

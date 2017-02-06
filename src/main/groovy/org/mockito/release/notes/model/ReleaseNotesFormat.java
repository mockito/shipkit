package org.mockito.release.notes.model;

import java.util.Map;

/**
 * Format options for the release notes
 */
public interface ReleaseNotesFormat {

    /**
     * Mapping of label to descriptive label summary.
     * Labels of the improvements (see {@link Improvement#getLabels()} are often short and not very descriptive.
     * In final release notes we want descriptive sections of improvements.
     * Mappings also allow controlling priority in presenting improvements -
     *  the formatter can use the order of label mappings. For example, 'noteworthy' labelled improvements on top.
     */
    Map<String, String> getLabelMapping();
}

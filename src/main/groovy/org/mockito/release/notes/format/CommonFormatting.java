package org.mockito.release.notes.format;

import org.mockito.release.notes.model.Improvement;

/**
 * Shared formatting
 */
class CommonFormatting {

    static String format(Improvement improvement) {
        return improvement.getTitle() + " [(#" + improvement.getId() + ")](" + improvement.getUrl() + ")";
    }
}

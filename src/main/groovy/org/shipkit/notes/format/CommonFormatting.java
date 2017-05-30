package org.shipkit.notes.format;

import org.shipkit.notes.model.Improvement;

/**
 * Shared formatting
 */
class CommonFormatting {

    static String format(Improvement improvement) {
        return improvement.getTitle() + " [(#" + improvement.getId() + ")](" + improvement.getUrl() + ")";
    }
}

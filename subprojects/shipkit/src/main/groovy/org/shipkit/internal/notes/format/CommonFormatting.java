package org.shipkit.internal.notes.format;

import org.shipkit.internal.notes.model.Improvement;

/**
 * Shared formatting
 */
class CommonFormatting {

    static String format(Improvement improvement) {
        return improvement.getTitle() + " [(#" + improvement.getId() + ")](" + improvement.getUrl() + ")";
    }
}

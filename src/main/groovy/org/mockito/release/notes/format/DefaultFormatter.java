package org.mockito.release.notes.format;

import org.mockito.release.notes.improvements.Improvement;

/**
 * Original formatter
 */
public class DefaultFormatter {

    public static String format(Improvement improvement) {
        return improvement.getTitle() + " [(#" + improvement.getId() + ")](" + improvement.getUrl() + ")";
    }
}

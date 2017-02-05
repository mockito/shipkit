package org.mockito.release.notes.format;

import org.mockito.release.notes.model.ReleaseNotesFormat;

import java.util.Map;

public class DefaultReleaseNotesFormat implements ReleaseNotesFormat {

    private final Map<String, String> labelMapping;

    public DefaultReleaseNotesFormat(Map<String, String> labelMapping) {
        this.labelMapping = labelMapping;
    }

    @Override
    public Map<String, String> getLabelMapping() {
        return labelMapping;
    }
}

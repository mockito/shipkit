package org.mockito.release.notes.format;

import org.mockito.release.notes.model.ReleaseNotesData;

import java.util.Map;

class DetailedFormatter implements MultiReleaseNotesFormatter {

    private final String introductionText;
    private final Map<String, String> labelMapping;
    private final String vcsCommitsLinkTemplate;

    DetailedFormatter(String introductionText, Map<String, String> labelMapping, String vcsCommitsLinkTemplate) {
        this.introductionText = introductionText;
        this.labelMapping = labelMapping;
        this.vcsCommitsLinkTemplate = vcsCommitsLinkTemplate;
    }

    @Override
    public String formatReleaseNotes(Iterable<ReleaseNotesData> data) {
        return null;
    }
}

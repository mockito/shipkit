package org.mockito.release.notes.format;

import org.mockito.release.notes.model.ReleaseNotesData;

class DetailedFormatter implements MultiReleaseNotesFormatter {

    private final String introductionText;
    private final String notableReleaseNotesLink;
    private final String vcsCommitsLinkTemplate;

    DetailedFormatter(String introductionText, String notableReleaseNotesLink, String vcsCommitsLinkTemplate) {
        this.introductionText = introductionText;
        this.notableReleaseNotesLink = notableReleaseNotesLink;
        this.vcsCommitsLinkTemplate = vcsCommitsLinkTemplate;
    }

    @Override
    public String formatReleaseNotes(Iterable<ReleaseNotesData> data) {
        return null;
    }
}

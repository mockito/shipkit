package org.shipkit.internal.notes.about;

import java.io.File;

class ReleaseNoteCounter {

    private final ReleaseNoteFileReader releaseNoteFileReader = new ReleaseNoteFileReader();
    private final CounterExtractor counterExtractor = new CounterExtractor();

    int getNextReleaseNoteNumber(File releaseNoteFile) {
        if (!releaseNoteFile.exists()) {
            return 1;
        }

        String firstLine = releaseNoteFileReader.getFirstLine(releaseNoteFile);
        int previousCounter = counterExtractor.getCounter(firstLine);
        return previousCounter + 1;
    }
}

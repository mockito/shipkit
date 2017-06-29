package org.shipkit.internal.notes.about;

import java.io.File;

class ReleaseNoteFileReader {

    String getFirstLine(File releaseNoteFile) {
        BufferedReaderWrapper br = new BufferedReaderWrapper(releaseNoteFile);
        String firstLine = readFirstLine(br);
        br.close();
        return firstLine;
    }

    private String readFirstLine(BufferedReaderWrapper bufferedReader) {
        String firstLine = bufferedReader.readLine();
        return (firstLine == null) ? "" : firstLine;
    }
}

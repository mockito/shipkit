package org.shipkit.internal.notes.about;

import java.io.File;
import java.io.IOException;

public class InfoAboutRemover {

    public void removeAboutInfoIfExist(File releseNoteFile) {
        try {
            File tempFile = File.createTempFile("tmp", "shipkit");

            BufferedReaderWrapper reader = new BufferedReaderWrapper(releseNoteFile);
            BufferedWriterWrapper writer = new BufferedWriterWrapper(tempFile);

            String currentLine;
            boolean firstLine = true;

            while ((currentLine = reader.readLine()) != null) {
                if (isInfoAboutLine(currentLine)) {
                    reader.readLine();
                    continue;
                }
                if (firstLine) {
                    firstLine = false;
                } else {
                    writer.write(System.getProperty("line.separator"));
                }
                writer.write(currentLine);

            }

            writer.flush();
            writer.close();
            reader.close();

            tempFile.renameTo(releseNoteFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isInfoAboutLine(String line) {
        return line.matches(CounterExtractor.ABOUT_INFO_PATTERN.pattern());
    }
}

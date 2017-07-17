package org.shipkit.internal.notes.header;

import java.io.*;

public class BufferedReaderWrapper {

    private final BufferedReader bufferedReader;

    public BufferedReaderWrapper(File file) {
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Can't create FileReader for " + file, e);
        }
    }

    public String readLine() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Can't read from file ", e);
        }
    }

    public void close() {
        try {
            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException("Can't close file reader", e);
        }
    }
}

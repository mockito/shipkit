package org.shipkit.internal.notes.about;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BufferedWriterWrapper {

    private final BufferedWriter bufferedWriter;

    public BufferedWriterWrapper(File file) {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new RuntimeException("Can't create FileWritter for " + file, e);
        }
    }

    public void write(String str) {
        try {
            bufferedWriter.write(str);
        } catch (IOException e) {
            throw new RuntimeException("Can't write to file", e);
        }
    }

    public void newLine() {
        try {
            bufferedWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Can't create new Line", e);
        }
    }

    public void close() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("Can't close file writer", e);
        }
    }

    public void flush() {
        try {
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Can't flush file writer", e);
        }
    }
}

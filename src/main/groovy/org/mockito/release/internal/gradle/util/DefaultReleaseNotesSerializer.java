package org.mockito.release.internal.gradle.util;

import org.mockito.release.notes.model.ReleaseNotesData;

import java.io.*;
import java.util.Collection;

public class DefaultReleaseNotesSerializer implements ReleaseNotesSerializer{

    private final File file;

    public DefaultReleaseNotesSerializer(File file){
        this.file = file;
    }

    public void serialize(Collection<ReleaseNotesData> releaseNotes){
        ObjectOutputStream out = null;
        try {
            out = getSerializedNotesOutputStream();
            out.writeObject(releaseNotes);
        } catch (IOException e) {
            throw new RuntimeException("Serialization of release notes failed", e);
        } finally{
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public Collection<ReleaseNotesData> deserialize() {
        ObjectInputStream objectInput = null;
        try {
            objectInput = getReleaseNotesFileStream();
            return (Collection<ReleaseNotesData>) objectInput.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Deserializing release notes from temporary file failed. " +
                    "Probably the file was not properly generated in the previous step.", e);
        } finally{
            if(objectInput != null) {
                try {
                    objectInput.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private ObjectOutputStream getSerializedNotesOutputStream() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(file);
        return new ObjectOutputStream(fileOut);
    }

    private ObjectInputStream getReleaseNotesFileStream() throws IOException {
        FileInputStream fileInput = new FileInputStream(file);
        return new ObjectInputStream(fileInput);
    }
}

package org.shipkit.internal.gradle.util;

import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {

    public static ZipFile openZipFile(File file) {
        try {
            return new ZipFile(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not open zip file " + file, e);
        }
    }

    public static boolean fileContainsEntry(File file, String entry) {
        ZipFile zip = null;
        try {
            zip = openZipFile(file);
            return zip.getEntry(entry) != null;
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                    throw new RuntimeException("Could not close zip file " + file, e);
                }
            }
        }
    }

    public static void closeZipFile(ZipFile file) {
        try {
            if (file != null) {
                file.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<String> extractEntries(ZipFile file) {
        Set<String> set = new LinkedHashSet<String>();
        for (Enumeration e = file.entries(); e.hasMoreElements();) {
            set.add(((ZipEntry) e.nextElement()).getName());
        }
        return set;
    }

    public static String readEntryContent(File file, String entry) {
        ZipFile zip = null;
        try {
            zip = openZipFile(file);
            return IOUtil.readFully(zip.getInputStream(zip.getEntry(entry)));
        } catch (IOException e) {
            throw new RuntimeException("Could not read entry " + entry + " in file " + file, e);
        } finally {
            ZipUtil.closeZipFile(zip);

        }
    }
}

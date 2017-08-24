package org.shipkit.internal.util;

import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class PropertiesUtil {

    public static Properties readProperties(File properties) {
        Properties p = new Properties();
        FileReader reader = null;
        try {
            reader = new FileReader(properties);
            p.load(reader);
        } catch (Exception e) {
            throw new RuntimeException("Problems reading properties file: " + properties, e);
        } finally {
            IOUtil.close(reader);
        }
        return p;
    }
}

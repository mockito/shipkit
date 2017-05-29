package org.mockito.release.internal.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Md5 utilities
 */
public class Md5 {

    /**
     * Calculates the md5 of a given file.
     * @param file the file to calculate the md5 for
     * @return the resulting md5
     */
    public static String calculate(File file) {
        InputStream is = null;
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            is = new FileInputStream(file);

            int bytesRead = 0;
            byte[] data = new byte[1024];
            while ((bytesRead = is.read(data)) != -1) {
                md.update(data, 0, bytesRead);
            }
            return new BigInteger(1, md.digest()).toString(16);
        } catch (java.io.IOException e) {
            throw new RuntimeException("error while generating md5 for file " + file, e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("error while generating md5 for file " + file, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

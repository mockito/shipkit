package org.mockito.release.internal.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date and Time utilities
 */
public class DateUtil {

    /**
     * Yesterday date from now
     */
    public static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    /**
     * Formats the date to the format required by GitHub API,
     * ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ
     */
    public static String forGitHub(Date date) {
        return new SimpleDateFormat("YYYY-MM-dd'T'HH:MM:ssZ").format(date);
    }
}

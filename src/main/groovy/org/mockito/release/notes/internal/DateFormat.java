package org.mockito.release.notes.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Date parsing and formatting utilities
 */
public class DateFormat {

    /**
     * Parses date in iso format, e.g. "yyyy-MM-dd HH:mm:ss Z"
     */
    public static Date parseDate(String date) {
        String pattern = "yyyy-MM-dd HH:mm:ss Z";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date.trim());
        } catch (ParseException e) {
            throw new RuntimeException("Problems parsing date: '" + date + "'. Required format is: '" + pattern + "'.", e);
        }
    }

    /**
     * Parses date in iso format, e.g. "yyyy-MM-dd HH:mm:ss Z"
     */
    public static String formatDate(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return f.format(date);
    }
}

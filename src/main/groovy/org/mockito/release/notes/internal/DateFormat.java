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
            throw new RuntimeException("Problems parsing date: [" + date + "]. Required format is: [" + pattern + "].", e);
        }
    }

    /**
     * Formats date to most reasonable format to show on the release notes
     */
    public static String formatDate(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return f.format(date);
    }

    /**
     * Parse Date in epoch seconds (Unix time).
     */
    public static Date parseDateInEpochSeconds(String date) {
        return new Date(Long.parseLong(date) * 1000);
    }

    /**
     * Formats date to local timezone to shows in debug logs
     */
    public static String formatDateToLocalTime(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm a z");
        f.setTimeZone(TimeZone.getDefault());
        return f.format(date);
    }
}

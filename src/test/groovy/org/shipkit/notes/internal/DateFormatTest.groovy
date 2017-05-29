package org.shipkit.notes.internal

import spock.lang.Specification

import java.text.SimpleDateFormat

class DateFormatTest extends Specification {

    def "parses date"() {
        def date = DateFormat.parseDate("2017-01-29 08:14:09 -0800")

        expect:
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        //Ensure that the date in UTC is correct
        f.format(date) == "2017-01-29 16:14"
    }

    def "parses UTC date"() {
        expect:
        DateFormat.formatDate(DateFormat.parseUTCDate("2017-01-15")) == "2017-01-15"
    }

    def "throws meaningful exception when date cannot be parsed"() {
        when:
        DateFormat.parseDate("2017-01- 08:14:09 -0800")

        then:
        def ex = thrown(RuntimeException)
        ex.message.contains("2017-01- 08:14:09 -0800")
    }
}

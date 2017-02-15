package org.mockito.release.notes.internal

import spock.lang.Specification

class DateFormatTest extends Specification {

    def "parses date"() {
        def date = DateFormat.parseDate("2017-01-29 08:14:09 -0800")

        expect:
        //Ensure that the TZ is correct
        DateFormat.formatDate(date) == "2017-01-29 16:14"
    }

    def "throws meaningful exception when date cannot be parsed"() {
        when:
        DateFormat.parseDate("2017-01- 08:14:09 -0800")

        then:
        def ex = thrown(RuntimeException)
        ex.message.contains("2017-01- 08:14:09 -0800")
    }
}

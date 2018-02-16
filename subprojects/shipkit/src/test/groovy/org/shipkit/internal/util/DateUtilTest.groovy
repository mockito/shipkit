package org.shipkit.internal.util

import spock.lang.Specification

import java.text.SimpleDateFormat

class DateUtilTest extends Specification {

    def "yesterday"() {
        expect: //smoke test, cannot reliably assert on yesterday
        DateUtil.yesterday()
    }

    def "gitHub format"() {
        def tz = TimeZone.getTimeZone("GMT")
        def cal = Calendar.getInstance(tz)
        cal.set(2017, 1, 1, 12, 0, 0)
        def date = cal.getTime()

        expect:
        DateUtil.forGitHub(date, tz) == "2017-02-01T12:00:00+0000"
    }

    def "parses date"() {
        def date = DateUtil.parseDate("2017-01-29 08:14:09 -0800")

        expect:
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm")
        f.setTimeZone(TimeZone.getTimeZone("UTC"))
        //Ensure that the date in UTC is correct
        f.format(date) == "2017-01-29 16:14"
    }

    def "parses UTC date"() {
        expect:
        DateUtil.formatDate(DateUtil.parseUTCDate("2017-01-15")) == "2017-01-15"
    }

    def "throws meaningful exception when date cannot be parsed"() {
        when:
        DateUtil.parseDate("2017-01- 08:14:09 -0800")

        then:
        def ex = thrown(RuntimeException)
        ex.message.contains("2017-01- 08:14:09 -0800")
    }
}

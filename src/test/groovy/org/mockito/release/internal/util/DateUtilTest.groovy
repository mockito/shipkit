package org.mockito.release.internal.util

import spock.lang.Specification

class DateUtilTest extends Specification {

    def "yesterday"() {
        expect: //smoke test, cannot reliably assert on yesterday
        DateUtil.yesterday()
    }

    def "gitHub format"() {
        //TODO it would be good to have a method for this on DateUtil class
        def tz = TimeZone.getTimeZone("GMT")
        def cal = Calendar.getInstance(tz)
        cal.set(2017, 1, 1, 12, 0, 0)
        def date = cal.getTime()

        expect:
        DateUtil.forGitHub(date, tz) == "2017-02-01T12:00:00+0000"
    }
}

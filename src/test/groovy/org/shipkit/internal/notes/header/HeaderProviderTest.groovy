package org.shipkit.internal.notes.header

import spock.lang.Specification

class HeaderProviderTest extends Specification {

    HeaderProvider testObj = new HeaderProvider()

    def "should return formatted header"() {
        when:
        String result = testObj.getHeader("some header");

        then:
        result == "<sup><sup>*some header*</sup></sup>\n\n"
    }
}

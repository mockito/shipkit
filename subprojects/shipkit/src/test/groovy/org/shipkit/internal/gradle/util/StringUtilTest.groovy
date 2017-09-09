package org.shipkit.internal.gradle.util

import spock.lang.Specification

class StringUtilTest extends Specification {

    def "knows if empty"() {
        expect:
        StringUtil.isEmpty(null)
        StringUtil.isEmpty("")
        !StringUtil.isEmpty(" ")
        !StringUtil.isEmpty("xx")
    }
}

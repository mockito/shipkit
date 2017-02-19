package org.mockito.release.notes.format

import spock.lang.Specification

class DetailedFormatterTest extends Specification {

    def f = new DetailedFormatter("Release notes: \n\n", "http://commits/{0}...{1}")

    def "no releases"() {

    }

    def "empty releases"() {

    }

    def "no improvements"() {

    }
}

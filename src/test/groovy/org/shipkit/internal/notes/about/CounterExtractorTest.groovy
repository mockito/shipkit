package org.shipkit.internal.notes.about

import spock.lang.Specification
import spock.lang.Unroll

class CounterExtractorTest extends Specification {

    private final CounterExtractor testObj = new CounterExtractor()

    @Unroll
    def "should extract correct couter from first line od realese note"() {
        expect:
        testObj.getCounter(infoAbout) == result
        where:
        infoAbout                                                  || result
        InformationAboutProvider.getInformationAbout(2)            || 2
        InformationAboutProvider.getInformationAbout(10)           || 10
        InformationAboutProvider.getCommentedInformationAbout(123) || 123
        InformationAboutProvider.getCommentedInformationAbout(4)   || 4
        ""                                                         || 0
        "some content"                                             || 0
        "some content with number 43 and 2323"                     || 0
    }

    def "about info pattern should match to information about shipkit"() {
        when:
        when:
        String infoAboutShipkit = InformationAboutProvider.getInformationAbout(300)
        then:
        infoAboutShipkit.matches(CounterExtractor.ABOUT_INFO_PATTERN)
    }

    def "about info pattern should match to commented information about shipkit"() {
        when:
        String infoAboutShipkit = InformationAboutProvider.getCommentedInformationAbout(300)
        then:
        infoAboutShipkit.matches(CounterExtractor.ABOUT_INFO_PATTERN)
    }
}

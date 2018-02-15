package org.shipkit.internal.notes.header

import spock.lang.Specification

class HeaderRemoverTest extends Specification {

    def "should remove header when exist"() {
        given:
        String content = new HeaderProvider().getHeader('some header with some text and numbers') + 'old content'

        when:
        String result = HeaderRemover.removeHeaderIfExist(content)

        then:
        result == "old content"
    }

    def "should copy new line and content when header not exist"() {
        given:
        String content = "old content\n\nold content second line"

        when:
        String result = HeaderRemover.removeHeaderIfExist(content)

        then:
        result == "old content\n\nold content second line"
    }
}

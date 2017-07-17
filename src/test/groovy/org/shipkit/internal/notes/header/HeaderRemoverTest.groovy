package org.shipkit.internal.notes.header

import spock.lang.Specification

class HeaderRemoverTest extends Specification {

    HeaderRemover testObj = new HeaderRemover();

    def "should remove header when exist"() {
        given:
        File releaseNoteFile = File.createTempFile("temp", ".tmp");
        releaseNoteFile.with {
            write new HeaderProvider().getHeader("some header with some text and numbers") + "old content"
        }
        when:
        testObj.removeHeaderIfExist(releaseNoteFile);

        then:
        releaseNoteFile.text == "old content"
    }

    def "should copy new line and content when header not exist"() {
        given:
        File releaseNoteFile = File.createTempFile("temp", ".tmp");
        releaseNoteFile.with {
            write "old content\n\nold content second line"
        }
        when:
        testObj.removeHeaderIfExist(releaseNoteFile);

        then:
        releaseNoteFile.text == "old content\n\nold content second line"
    }
}

package org.shipkit.internal.notes.about

import spock.lang.Specification

class InfoAboutRemoverTest extends Specification {

    InfoAboutRemover infoAboutRemover = new InfoAboutRemover();

    def "should remove about info when exist and it's commented"() {
        given:
        File releaseNoteFile = File.createTempFile("temp", ".tmp");
        releaseNoteFile.with {
            write InformationAboutProvider.getInformationAbout(400) + "old content"
        }
        when:
        infoAboutRemover.removeAboutInfoIfExist(releaseNoteFile);

        then:
        releaseNoteFile.text == "old content"
    }

    def "should remove about info when exist and it's not commented"() {
        given:
        File releaseNoteFile = File.createTempFile("temp", ".tmp");
        releaseNoteFile.with {
            write InformationAboutProvider.getCommentedInformationAbout(400) + "old content"
        }
        when:
        infoAboutRemover.removeAboutInfoIfExist(releaseNoteFile);

        then:
        releaseNoteFile.text == "old content"
    }

    def "should copy new line and content when about info not exist"() {
        given:
        File releaseNoteFile = File.createTempFile("temp", ".tmp");
        releaseNoteFile.with {
            write "old content\n\nold content second line"
        }
        when:
        infoAboutRemover.removeAboutInfoIfExist(releaseNoteFile);

        then:
        releaseNoteFile.text == "old content\n\nold content second line"
    }
}

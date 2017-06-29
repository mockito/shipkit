package org.shipkit.internal.notes.about

import spock.lang.Specification
import spock.lang.Unroll

class InformationAboutProviderTest extends Specification {

    private final InformationAboutProvider testObj = new InformationAboutProvider();

    @Unroll
    def "should generate correct info about shipkit when release note file not exist"() {
        given:
        File releaseNoteFile = new File("notExistFile.neg")

        expect:
        "should generate correct info about shipkit when information about is " + informationAbout + " and release not file not exist"
        testObj.getInformationAboutText(releaseNoteFile, informationAbout) == result
        where:
        informationAbout || result
        true             || InformationAboutProvider.getInformationAbout(1)
        false            || InformationAboutProvider.getCommentedInformationAbout(1)
    }

    @Unroll
    def "should generate correct info about shipkit when release note file is empty"() {
        given:
        File releaseNoteFile = File.createTempFile("temp", ".tmp")

        expect:
        "should generate correct info about shipkit when information about is " + informationAbout + " and release not file not exist"
        testObj.getInformationAboutText(releaseNoteFile, informationAbout) == result
        where:
        informationAbout || result
        true             || InformationAboutProvider.getInformationAbout(1)
        false            || InformationAboutProvider.getCommentedInformationAbout(1)
    }

    @Unroll
    def "should generate correct info about shipkit when release note file exist"() {
        given:
        File releaseNoteFile = File.createTempFile("temp", ".tmp");
        releaseNoteFile.with {
            write content
        }

        expect:
        testObj.getInformationAboutText(releaseNoteFile, informationAbout) == result
        where:
        informationAbout | content                                                                          || result
        true             | "content"                                                                        || InformationAboutProvider.getInformationAbout(1)
        false            | "content"                                                                        || InformationAboutProvider.getCommentedInformationAbout(1)
        true             | InformationAboutProvider.getInformationAbout(1) + "/n new content line"          || InformationAboutProvider.getInformationAbout(2)
        true             | InformationAboutProvider.getCommentedInformationAbout(1) + "/n new content line" || InformationAboutProvider.getInformationAbout(2)
        false            | InformationAboutProvider.getInformationAbout(1) + "/n new content line"          || InformationAboutProvider.getCommentedInformationAbout(2)
        false            | InformationAboutProvider.getCommentedInformationAbout(1) + "/n new content line" || InformationAboutProvider.getCommentedInformationAbout(2)
    }
}

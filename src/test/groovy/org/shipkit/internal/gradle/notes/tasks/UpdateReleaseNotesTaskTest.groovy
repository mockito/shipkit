package org.shipkit.internal.gradle.notes.tasks

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class UpdateReleaseNotesTaskTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()
    UpdateReleaseNotes update = new UpdateReleaseNotes()

    def "should update release notes if not in preview mode" (){
        def f = tmp.newFile("release-notes.md")

        when:
        update.updateReleaseNotes(false, f, "content")

        then:
        f.text == "content"
    }

    def "should not modify releaseNotesFile if in preview mode" (){
        def f = tmp.newFile("release-notes.md")

        when:
        update.updateReleaseNotes(true, f, "content")

        then:
        f.text.isEmpty()
    }
}

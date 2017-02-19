package org.mockito.release.notes.format

import org.mockito.release.notes.internal.DefaultReleaseNotesData
import org.mockito.release.notes.model.ContributionSet
import spock.lang.Specification

class DetailedFormatterTest extends Specification {

    def f = new DetailedFormatter("Release notes:\n\n", ["noteworthy": "Noteworthy", "bug": "Bugfixes"], "http://commits/{0}...{1}")

    def "no releases"() {
        expect:
        f.formatReleaseNotes([]) == """Release notes:

No release information."""
    }

    def "empty releases"() {
        def d1 = new DefaultReleaseNotesData("2.0.0", new Date(1483500000000), Stub(ContributionSet), [], "v1.9.0", "v2.0.0")
        def d2 = new DefaultReleaseNotesData("1.9.0", new Date(1483100000000), Stub(ContributionSet), [], "v1.8.0", "v1.9.0")

        expect:
        f.formatReleaseNotes([d1, d2]) == """Release notes:

**2.0.0** - no code changes (no commits) - *2017-01-04*

**1.9.0** - no code changes (no commits) - *2016-12-30*"""
    }

    def "no improvements"() {

    }
}

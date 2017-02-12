package org.mockito.release.notes.internal

import org.mockito.release.notes.format.ReleaseNotesFormatters
import org.mockito.release.notes.generator.ReleaseNotesGenerators
import org.mockito.release.notes.improvements.ImprovementsProvider
import org.mockito.release.notes.model.ContributionSet
import org.mockito.release.notes.model.Improvement
import org.mockito.release.notes.vcs.ContributionsProvider
import spock.lang.Ignore
import spock.lang.Specification

class DefaultReleaseNotesGeneratorTest extends Specification {

    def "generates release notes"() {
        def contributionsProvider = Mock(ContributionsProvider)
        def improvementsProvider = Mock(ImprovementsProvider)
        def gen = new DefaultReleaseNotesGenerator(contributionsProvider, improvementsProvider)
        def c1 = Stub(ContributionSet), c2 = Stub(ContributionSet)
        def i1 = [Stub(Improvement)], i2 = [Stub(Improvement)]

        when:
        def notes = gen.generateReleaseNotes("1.0.0", ["1.1.0", "1.2.0"], "v", ["bugfix"])

        then:
        1 * contributionsProvider.getContributionsBetween("v1.0.0", "v1.1.0") >> c1
        1 * contributionsProvider.getContributionsBetween("v1.1.0", "v1.2.0") >> c2
        0 * contributionsProvider._

        1 * improvementsProvider.getImprovements(c1, ["bugfix"] ) >> i1
        1 * improvementsProvider.getImprovements(c2, ["bugfix"]) >> i2
        0 * improvementsProvider._

        notes.size() == 2
    }

    @Ignore //delete when work is done
    def "lifecycle test"() {
        def workDir = new File("/Users/sfaber/mockito/src");
        def authToken = "a0a4c0f41c200f7c653323014d6a72a127764e17"
        def gen = ReleaseNotesGenerators.releaseNotesGenerator(workDir, authToken)
        def notes = gen.generateReleaseNotes("2.4.0", ["2.5.0", "2.6.1", "2.7.0"], "v", ["noteworthy"])

        expect:
        println ReleaseNotesFormatters.conciseFormatter().formatReleaseNotes(notes)
    }
}

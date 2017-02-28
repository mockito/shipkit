package org.mockito.release.notes.generator

import org.mockito.release.notes.format.ReleaseNotesFormatters
import org.mockito.release.notes.improvements.ImprovementsProvider
import org.mockito.release.notes.model.ContributionSet
import org.mockito.release.notes.model.Improvement
import org.mockito.release.notes.vcs.ContributionsProvider
import org.mockito.release.notes.vcs.ReleaseDateProvider
import spock.lang.Ignore
import spock.lang.Specification

class DefaultReleaseNotesGeneratorTest extends Specification {

    def contributionsProvider = Mock(ContributionsProvider)
    def improvementsProvider = Mock(ImprovementsProvider)
    def releaseDateProvider = Stub(ReleaseDateProvider)
    def gen = new DefaultReleaseNotesGenerator(contributionsProvider, improvementsProvider, releaseDateProvider)

    def "generates release notes"() {
        def c1 = Stub(ContributionSet), c2 = Stub(ContributionSet)
        def i1 = [Stub(Improvement)], i2 = [Stub(Improvement)]
        def date1 = new Date(1487000000000), date2 = new Date(1488000000000)

        when:
        def notes = gen.generateReleaseNotesData(["1.2.0", "1.1.0", "1.0.0"], "v", ["bugfix"], true)

        then:
        releaseDateProvider.getReleaseDates(["1.2.0", "1.1.0", "1.0.0"], "v") >> ["1.2.0": date1, "1.1.0": date2]

        1 * contributionsProvider.getContributionsBetween("v1.1.0", "v1.2.0") >> c1
        1 * contributionsProvider.getContributionsBetween("v1.0.0", "v1.1.0") >> c2
        0 * contributionsProvider._

        1 * improvementsProvider.getImprovements(c1, ["bugfix"], true) >> i1
        1 * improvementsProvider.getImprovements(c2, ["bugfix"], true) >> i2
        0 * improvementsProvider._

        notes.size() == 2
        notes[0].version == "1.2.0"
        notes[0].date == date1
        notes[1].version == "1.1.0"
        notes[1].date == date2
    }

    def "generates single release notes with no tag prefix"() {
        def c1 = Stub(ContributionSet)
        def i1 = [Stub(Improvement)]

        when:
        def notes = gen.generateReleaseNotesData(["1.1.0", "1.0.0"], "", ["notable"], false)

        then:
        1 * contributionsProvider.getContributionsBetween("1.0.0", "1.1.0") >> c1
        1 * improvementsProvider.getImprovements(c1, ["notable"], false) >> i1

        notes.size() == 1
    }

    @Ignore
    def "lifecycle test"() {
        def workDir = new File("/Users/sfaber/mockito/src");
        def authToken = "a0a4c0f41c200f7c653323014d6a72a127764e17"
        def gen = ReleaseNotesGenerators.releaseNotesGenerator(workDir, authToken)
        def notes = gen.generateReleaseNotesData(["2.7.0", "2.6.1", "2.5.0", "2.4.0"], "v", ["noteworthy"], true)

        expect:
        println ReleaseNotesFormatters.notableFormatter("Release notes:\n\n").formatReleaseNotes(notes)
    }
}

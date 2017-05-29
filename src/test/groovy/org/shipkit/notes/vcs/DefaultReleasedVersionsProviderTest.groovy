package org.shipkit.notes.vcs

import org.shipkit.exec.ProcessRunner
import org.shipkit.notes.internal.DateFormat
import spock.lang.Specification
import spock.lang.Unroll

class DefaultReleasedVersionsProviderTest extends Specification {

    def dateProvider = Mock(RevisionDateProvider)
    def provider = new DefaultReleasedVersionsProvider(dateProvider)
    def someDate = new Date(1491100000000)

    @Unroll
    def "illegal arguments"() {
        when:
        notEnoughVersions.call()

        then:
        thrown(IllegalArgumentException)

        where:
        notEnoughVersions << [
            { new DefaultReleasedVersionsProvider(Stub(ProcessRunner))
                    .getReleasedVersions(null, new Date(), [], "v") },
            { new DefaultReleasedVersionsProvider(Stub(ProcessRunner))
                    .getReleasedVersions(null, new Date(), ['1.0'], "v") },
            { new DefaultReleasedVersionsProvider(Stub(ProcessRunner))
                    .getReleasedVersions('1.0', new Date(), [], "v") },
            { new DefaultReleasedVersionsProvider(Stub(ProcessRunner))
                    .getReleasedVersions('1.0', null, ['1.1'], "v") }
        ]
    }

    def "provides versions "() {
        dateProvider.getDate("v2.0.0") >> DateFormat.parseUTCDate("2017-02-15")
        dateProvider.getDate("v1.5.0") >> DateFormat.parseUTCDate("2017-01-30")
        dateProvider.getDate("v1.0.0") >> DateFormat.parseUTCDate("2017-01-15")

        expect:
        //with head version
        provider.getReleasedVersions("2.0.0", someDate, ["1.5.0", "1.0.0"], "v").toString() ==
                "[2.0.0@2017-04-02(HEAD..v1.5.0), 1.5.0@2017-01-30(v1.5.0..v1.0.0), 1.0.0@2017-01-15(v1.0.0..null)]"

        //no head version
        provider.getReleasedVersions(null, null, ["2.0.0", "1.5.0", "1.0.0"], "v").toString() ==
                "[2.0.0@2017-02-15(v2.0.0..v1.5.0), 1.5.0@2017-01-30(v1.5.0..v1.0.0), 1.0.0@2017-01-15(v1.0.0..null)]"
    }
}
package org.mockito.release.notes.vcs

import org.mockito.release.exec.ProcessRunner
import org.mockito.release.notes.internal.DateFormat
import spock.lang.Specification

class DefaultReleaseDateProviderTest extends Specification {

    def runner = Mock(ProcessRunner)
    def provider = new DefaultReleaseDateProvider(runner)

    def "no versions"() {
        expect:
        provider.getReleaseDates([], "v").isEmpty()
    }

    def "provides release dates"() {
        runner.run("git", "log", "--pretty=%ad", "--date=iso", "v1.0.0", "-n", "1") >> "\n2017-01-29 08:14:09 -0800\n"
        runner.run("git", "log", "--pretty=%ad", "--date=iso", "v2.0.0", "-n", "1") >> "\n2017-01-30 10:14:09 -0400\n"

        when:
        def dates = provider.getReleaseDates(["1.0.0", "2.0.0"], "v")

        then:
        dates.size() == 2
        DateFormat.formatDate(dates["1.0.0"]) == "2017-01-29 16:14"
        DateFormat.formatDate(dates["2.0.0"]) == "2017-01-30 14:14"
    }
}
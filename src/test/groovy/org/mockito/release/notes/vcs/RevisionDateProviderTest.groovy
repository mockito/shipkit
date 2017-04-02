package org.mockito.release.notes.vcs

import org.mockito.release.exec.ProcessRunner
import org.mockito.release.notes.internal.DateFormat
import spock.lang.Specification

class RevisionDateProviderTest extends Specification {

    def runner = Mock(ProcessRunner)
    def provider = new RevisionDateProvider(runner)

    def "provides revision dates"() {
        runner.run("git", "log", "--pretty=%ad", "--date=iso", "v1.0.0", "-n", "1") >> "\n2017-01-29 08:14:09 -0800\n"
        runner.run("git", "log", "--pretty=%ad", "--date=iso", "v2.0.0", "-n", "1") >> "\n2017-01-30 10:14:09 -0400\n"

        expect:
        DateFormat.formatDate(provider.getDate("v1.0.0")) == "2017-01-29"
        DateFormat.formatDate(provider.getDate("v2.0.0")) == "2017-01-30"
    }
}

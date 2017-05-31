package org.shipkit.internal.notes.vcs

import org.shipkit.internal.exec.ProcessRunner
import org.shipkit.internal.notes.internal.DateFormat
import spock.lang.Specification

class RevisionDateProviderTest extends Specification {

    def runner = Mock(ProcessRunner)
    def provider = new RevisionDateProvider(runner)

    def "provides revision dates"() {
        runner.run("git", "log", "--pretty=%ad", "--date=iso", "v1.0.0", "-n", "1") >> "\n2017-01-29 08:14:09 -0800\n"
        runner.run("git", "log", "--pretty=%ad", "--date=iso", "v2.0.0", "-n", "1") >> "\n2017-01-30 10:14:09 -0400\n"
        runner.run("git", "log", "--pretty=%ad", "--date=iso", "v3.0.0", "-n", "1") >> "2017-04-11 13:59:59 +0000"

        expect:
        DateFormat.formatDate(provider.getDate("v1.0.0")) == "2017-01-29"
        DateFormat.formatDate(provider.getDate("v2.0.0")) == "2017-01-30"
        DateFormat.formatDate(provider.getDate("v3.0.0")) == "2017-04-11"
    }

    def "fails if revision number is incorrect what causes git call to fail"() {
        runner.run("git", "log", "--pretty=%ad", "--date=iso", "v1.0.0", "-n", "1") >> "fatal: ambiguous argument 'v1.0.0'"

        when:
        provider.getDate("v1.0.0")

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Can't get a proper date for revision number v1.0.0." +
                " Are you sure this revision or tag exists?" +
                " Following output was returned by git:\n" +
                "fatal: ambiguous argument 'v1.0.0'"
    }
}

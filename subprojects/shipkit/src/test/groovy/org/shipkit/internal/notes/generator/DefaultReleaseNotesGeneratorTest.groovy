package org.shipkit.internal.notes.generator

import org.shipkit.internal.notes.format.ReleaseNotesFormatters
import org.shipkit.internal.notes.vcs.IgnoredCommit
import spock.lang.Ignore
import spock.lang.Specification

class DefaultReleaseNotesGeneratorTest extends Specification {

    @Ignore //TODO make it a proper integ test
    def "gets release notes data"() {
        File rootDir = findRootDir()
        rootDir = new File("/Users/sfaber/mockito/example-release")

        def authToken = "e7fe8fcdd6ffed5c38498c4c79b2a68e6f6ed1bb"
        def gen = ReleaseNotesGenerators.releaseNotesGenerator(rootDir, "mockito/shipkit-example", authToken, new IgnoredCommit(["[ci skip]"]))
        def notes = gen.generateReleaseNotesData(null, ["0.10.0", "0.7.1", "0.0.1"], "v", ["noteworthy"], true)

        expect:
        def results = ReleaseNotesFormatters.notableFormatter(
                "Release notes:\n\n", "http://release-notes",
                "https://github.com/mockito/shipkit-example/compare/{0}...{1}")
                .formatReleaseNotes(notes)
        println results
        results == """Release notes:

### 0.10.0 - 2017-03-20

Authors: [1](http://release-notes), commits: [20](https://github.com/mockito/shipkit-example/compare/v0.7.1...v0.10.0), improvements: [3](http://release-notes).

 * Pom customization based on Mockito project [(#16)](https://github.com/mockito/shipkit-example/pull/16)
 * Javadoc and source publishing [(#15)](https://github.com/mockito/shipkit-example/pull/15)
 * Made it possible to publish to notable repo [(#14)](https://github.com/mockito/shipkit-example/pull/14)

### 0.7.1 - 2017-03-15

Authors: [1](http://release-notes), commits: [61](https://github.com/mockito/shipkit-example/compare/v0.0.1...v0.7.1), improvements: [2](http://release-notes).

 * Extracted release automation to script plugin [(#8)](https://github.com/mockito/shipkit-example/pull/8)
 * Set up release automation [(#7)](https://github.com/mockito/shipkit-example/pull/7)

"""
    }

    private static File findRootDir() {
        def rootDir = new File(".")
        while (!new File(rootDir, ".git").isDirectory()) {
            rootDir = rootDir.parentFile
            assert rootDir != null
        }
        rootDir
    }
}

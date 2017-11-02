package org.shipkit.internal.comparison

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.internal.comparison.diff.Diff
import spock.lang.Specification

class ComparePublicationsResultFormatterTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    ComparePublicationsResultFormatter formatter = new ComparePublicationsResultFormatter()

    File previousFile
    File currentFile

    void setup () {
        previousFile = tmp.newFile("previous")
        currentFile = tmp.newFile("current")
    }

    def "should format when files for both comparison are different" () {
        def sourcesJarDiff = Diff.ofDifferentFiles("sources jar diff output")
        def depInfoDiff = Diff.ofDifferentFiles("dependency info files diff output")

        when:
        def result = formatter.formatResults(previousFile, currentFile, sourcesJarDiff, depInfoDiff)

        then:
        result ==
"""  Differences between files:
  --- ${previousFile.absolutePath}
  +++ ${currentFile.absolutePath}

    sources jar diff output

  Differences between files:
  --- ${previousFile.absolutePath}/META-INF/dependency-info.md
  +++ ${currentFile.absolutePath}/META-INF/dependency-info.md

    Here you can see the changes in declared runtime dependencies between versions.

    dependency info files diff output"""
    }

    def "should format when only sources jars are different" () {
        def sourcesJarDiff = Diff.ofDifferentFiles("sources jar diff output")
        def depInfoDiff = Diff.ofEqualFiles()

        when:
        def result = formatter.formatResults(previousFile, currentFile, sourcesJarDiff, depInfoDiff)

        then:
        result ==
            """  Differences between files:
  --- ${previousFile.absolutePath}
  +++ ${currentFile.absolutePath}

    sources jar diff output

  Differences between files:
  --- ${previousFile.absolutePath}/META-INF/dependency-info.md
  +++ ${currentFile.absolutePath}/META-INF/dependency-info.md

    No differences."""
    }

    def "should format when only dependency info files are different" () {
        def sourcesJarDiff = Diff.ofEqualFiles()
        def depInfoDiff = Diff.ofDifferentFiles("dependency info files diff output")

        when:
        def result = formatter.formatResults(previousFile, currentFile, sourcesJarDiff, depInfoDiff)

        then:
        result ==
            """  Differences between files:
  --- ${previousFile.absolutePath}
  +++ ${currentFile.absolutePath}

    No differences.

  Differences between files:
  --- ${previousFile.absolutePath}/META-INF/dependency-info.md
  +++ ${currentFile.absolutePath}/META-INF/dependency-info.md

    Here you can see the changes in declared runtime dependencies between versions.

    dependency info files diff output"""
    }

    def "should format when all files are equal" () {
        def sourcesJarDiff = Diff.ofEqualFiles()
        def depInfoDiff = Diff.ofEqualFiles()

        when:
        def result = formatter.formatResults(previousFile, currentFile, sourcesJarDiff, depInfoDiff)

        then:
        result ==
            """  Differences between files:
  --- ${previousFile.absolutePath}
  +++ ${currentFile.absolutePath}

    No differences.

  Differences between files:
  --- ${previousFile.absolutePath}/META-INF/dependency-info.md
  +++ ${currentFile.absolutePath}/META-INF/dependency-info.md

    No differences."""
    }
}

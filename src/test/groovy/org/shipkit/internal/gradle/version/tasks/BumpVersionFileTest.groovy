package org.shipkit.internal.gradle.version.tasks

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.internal.version.Version
import spock.lang.Specification

class BumpVersionFileTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    def "shows informative message"() {
        def versionFile = tmp.newFile("version.properties")
        versionFile << "version=1.0.1\npreviousVersion=1.0.0"
        def info = Version.versionInfo(versionFile)

        expect:
        BumpVersionFile.versionMessage(info, "version.properties", ":bumpVersionFile") == """:bumpVersionFile - updated version file 'version.properties'
  - new version: 1.0.1
  - previous version: 1.0.0"""
    }
}

package org.shipkit.internal.gradle.util

import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import static org.shipkit.internal.gradle.util.BintrayUtil.getRepoLink

class BintrayUtilTest extends Specification {

    def project = new ProjectBuilder().build()

    def "link with org"() {
        BintrayExtension b = new BintrayExtension(project)
        b.pkg.repo = "maven"
        b.pkg.name = "mockito-core"
        b.pkg.userOrg = "mockito"

        expect:
        getRepoLink(b) == "https://bintray.com/mockito/maven/mockito-core/"
    }

    def "link without org"() {
        BintrayExtension b = new BintrayExtension(project)
        b.pkg.repo = "maven"
        b.pkg.name = "mockito-core"
        b.user = "szczepiq"

        expect:
        getRepoLink(b) == "https://bintray.com/szczepiq/maven/mockito-core/"
    }

    def "link without unconfigured extension"() {
        BintrayExtension b = new BintrayExtension(project)

        expect: //does not blow up
        getRepoLink(b)
    }
}

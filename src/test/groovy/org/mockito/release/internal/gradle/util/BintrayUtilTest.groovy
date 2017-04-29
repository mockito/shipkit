package org.mockito.release.internal.gradle.util

import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import static org.mockito.release.internal.gradle.util.BintrayUtil.getMarkdownRepoLink

class BintrayUtilTest extends Specification {

    def project = new ProjectBuilder().build()

    def "link with org"() {
        BintrayExtension b = new BintrayExtension(project)
        b.pkg.repo = "maven"
        b.pkg.name = "mockito-core"
        b.pkg.userOrg = "mockito"

        expect:
        getMarkdownRepoLink(b) == "[maven/mockito-core](https://bintray.com/mockito/maven/mockito-core)"
    }

    def "link without org"() {
        BintrayExtension b = new BintrayExtension(project)
        b.pkg.repo = "maven"
        b.pkg.name = "mockito-core"
        b.user = "szczepiq"

        expect:
        getMarkdownRepoLink(b) == "[maven/mockito-core](https://bintray.com/szczepiq/maven/mockito-core)"
    }

    def "link without unconfigured extension"() {
        BintrayExtension b = new BintrayExtension(project)

        expect: //does not blow up
        getMarkdownRepoLink(b)
    }
}

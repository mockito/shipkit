package org.mockito.release.internal.gradle.util

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ExtContainerTest extends Specification {

    def project = new ProjectBuilder().build()
    def ext = new ExtContainer(project)

    def "provides bintray repo"() {
        when: project.ext.'bintray_repo' = 'all'
        then: ext.bintrayRepo == 'all'

        when: project.ext.'bintray_notableRepo' = 'notable'
        then: ext.bintrayRepo == 'all'

        when: project.ext.'release_notable' = 'true'
        then: ext.bintrayRepo == 'notable'
    }

    def "provides default notable repo"() {
        when:
        project.ext.'bintray_repo' = 'repo'
        project.ext.'release_notable' = 'true'

        then: ext.bintrayRepo == 'repo'
    }

    def "provides pkg"() {
        when: project.ext.'bintray_pkg' = 'all'
        then: ext.bintrayPkgName == 'all'

        when:
        project.ext.'bintray_notablePkg' = 'notable'
        project.ext.'release_notable' = 'true'

        then: ext.bintrayPkgName == 'notable'
    }
}

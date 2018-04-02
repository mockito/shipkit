package org.shipkit.internal.gradle.util

import org.gradle.api.GradleException
import org.gradle.api.Project
import spock.lang.Specification

class ProjectUtilTest extends Specification {

    def "requireRootProject check passes for rootProject"() {
        def project = Mock(Project)

        when:
        ProjectUtil.requireRootProject(project, ProjectUtilTest)

        then:
        noExceptionThrown()
    }

    def "requireRootProject fails for non-rootProject"() {
        def project = Mock(Project)
        project.path >> ':subproject'

        when:
        project.getParent() >> Mock(Project)
        ProjectUtil.requireRootProject(project, ProjectUtilTest)

        then:
        GradleException ex = thrown(GradleException)
        ex.message.contains('intended to be applied only root project')
    }
}

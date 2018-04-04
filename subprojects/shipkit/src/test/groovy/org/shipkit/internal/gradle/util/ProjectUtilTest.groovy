package org.shipkit.internal.gradle.util

import org.gradle.api.GradleException
import org.gradle.api.Project
import spock.lang.Specification

class ProjectUtilTest extends Specification {

    def project = Mock(Project)

    def "requireRootProject check passes for rootProject"() {
        when:
        ProjectUtil.requireRootProject(project, ProjectUtilTest)

        then:
        noExceptionThrown()
    }

    def "requireRootProject fails for non-rootProject"() {
        when:
        project.getParent() >> Mock(Project)
        ProjectUtil.requireRootProject(project, ProjectUtilTest)

        then:
        GradleException ex = thrown(GradleException)
        ex.message.contains('intended to be applied only root project')
    }
}

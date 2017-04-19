package org.mockito.release.internal.gradle.configuration

import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import static org.mockito.release.internal.gradle.configuration.LazyValidator.getConfigurer

class LazyValidatorTest extends Specification {

    def project1 = new ProjectBuilder().build()
    def project2 = new ProjectBuilder().withParent(project1).build()

    def "there is only one configurer"() {
        expect:
        getConfigurer(project1) == getConfigurer(project2)
    }

    def "configures tasks lazily"() {
        given:
        def foo = project1.task("foo")
        def bar = project1.task("bar")
        getConfigurer(project1).configureLazily(foo, { foo.description = "foo task" } as Runnable)
        getConfigurer(project1).configureLazily(bar, { bar.description = "bar task" } as Runnable)

        when:
        getConfigurer(project1).graphPopulated(Stub(TaskExecutionGraph) {
            hasTask(foo) >> true
            hasTask(bar) >> false
        })

        then:
        foo.description == "foo task"
        bar.description == null //configuration not triggered because task was not in graph
    }
}

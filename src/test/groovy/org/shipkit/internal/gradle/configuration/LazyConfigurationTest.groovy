package org.shipkit.internal.gradle.configuration

import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import static org.shipkit.internal.gradle.configuration.LazyConfiguration.forceConfiguration
import static org.shipkit.internal.gradle.configuration.LazyConfiguration.getConfigurer
import static org.shipkit.internal.gradle.configuration.LazyConfiguration.lazyConfiguration

class LazyConfigurationTest extends Specification {

    def project1 = new ProjectBuilder().build()
    def project2 = new ProjectBuilder().withParent(project1).build()

    def "there is only one configurer"() {
        expect:
        getConfigurer(project1) == getConfigurer(project2)
    }

    def "configures lazily"() {
        def foo = project1.task("foo")

        when:
        lazyConfiguration(foo, { foo.description = "foo" } as Runnable)

        then:
        foo.description == null

        when:
        forceConfiguration(foo)

        then:
        foo.description == "foo"
    }

    def "works with task graph"() {
        def foo = project1.task("foo")
        def bar = project1.task("bar")

        lazyConfiguration(foo, { foo.description = "foo" } as Runnable)
        lazyConfiguration(bar, { bar.description = "bar" } as Runnable)

        when: //simulate task graph with foo but not bar task
        getConfigurer(project1).listener.graphPopulated(Stub(TaskExecutionGraph) {
            hasTask(foo) >> true
            hasTask(bar) >> false
        })

        then:
        foo.description == "foo"
        bar.description == null
    }
}

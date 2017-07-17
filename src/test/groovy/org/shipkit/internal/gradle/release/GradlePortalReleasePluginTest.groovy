package org.shipkit.internal.gradle.release

import org.gradle.api.GradleException
import org.shipkit.internal.gradle.configuration.LazyConfiguration
import org.shipkit.internal.util.EnvVariables
import testutil.PluginSpecification

import static org.shipkit.internal.gradle.release.GradlePortalReleasePlugin.PUBLISH_PLUGINS_TASK

class GradlePortalReleasePluginTest extends PluginSpecification {

    def env = Mock(EnvVariables)

    def "validates publish key"() {
        new GradlePortalReleasePlugin(env).apply(project)

        when:
        LazyConfiguration.forceConfiguration(project.tasks[PUBLISH_PLUGINS_TASK])

        then:
        def ex = thrown(GradleException)
        ex.message == """Gradle Plugin Portal 'gradle.publish.key' is required. Options:
 - export 'GRADLE_PUBLISH_KEY' env var (recommended for CI, don't commit secrets to VCS!)
 - use 'gradle.publish.key' project property"""
    }

    def "validates publish secret"() {
        new GradlePortalReleasePlugin(env).apply(project)
        project.ext.set(GradlePortalReleasePlugin.PUBLISH_KEY_PROPERTY, "key")

        when:
        LazyConfiguration.forceConfiguration(project.tasks[PUBLISH_PLUGINS_TASK])

        then:
        def ex = thrown(GradleException)
        ex.message == """Gradle Plugin Portal 'gradle.publish.secret' is required. Options:
 - export 'GRADLE_PUBLISH_SECRET' env var (recommended for CI, don't commit secrets to VCS!)
 - use 'gradle.publish.secret' project property"""
    }

    def "uses existing project properties"() {
        new GradlePortalReleasePlugin(env).apply(project)
        project.ext.set(GradlePortalReleasePlugin.PUBLISH_KEY_PROPERTY, "key")
        project.ext.set(GradlePortalReleasePlugin.PUBLISH_SECRET_PROPERTY, "secret")

        expect: //no exception thrown
        LazyConfiguration.forceConfiguration(project.tasks[PUBLISH_PLUGINS_TASK])
    }

    def "sets key based on env var"() {
        env.getNonEmptyEnv(GradlePortalReleasePlugin.PUBLISH_KEY_ENV) >> "123"
        env.getNonEmptyEnv(GradlePortalReleasePlugin.PUBLISH_SECRET_ENV) >> "abc"

        when:
        new GradlePortalReleasePlugin(env).apply(project)
        LazyConfiguration.forceConfiguration(project.tasks[PUBLISH_PLUGINS_TASK])

        then:
        project.ext.get(GradlePortalReleasePlugin.PUBLISH_KEY_PROPERTY) == "123"
        project.ext.get(GradlePortalReleasePlugin.PUBLISH_SECRET_PROPERTY) == "abc"
    }

    def "dry run effectively disables the task"() {
        project.shipkit.dryRun = true
        project.plugins.apply(GradlePortalReleasePlugin.class)

        when:
        project.tasks[PUBLISH_PLUGINS_TASK].execute()

        then:
        noExceptionThrown() //normally the task would fail because it is not configured
    }
}

package org.shipkit.internal.gradle.release

import org.gradle.api.GradleException
import org.shipkit.gradle.release.GradlePortalPublishTask
import org.shipkit.internal.gradle.configuration.LazyConfiguration
import org.shipkit.internal.util.EnvVariables
import testutil.PluginSpecification

import static org.shipkit.internal.gradle.release.GradlePortalReleasePlugin.PERFORM_PUBLISH_TASK

class GradlePortalReleasePluginTest extends PluginSpecification {

    def env = Mock(EnvVariables)

    def "applies"() {
        expect:
        project.plugins.apply(GradlePortalReleasePlugin.class)
    }

    def "validates publish key"() {
        project.plugins.apply(GradlePortalReleasePlugin.class)

        when:
        LazyConfiguration.forceConfiguration(project.tasks[PERFORM_PUBLISH_TASK])

        then:
        def ex = thrown(GradleException)
        ex.message == "Gradle Plugin Portal 'publishKey' is required. Export 'GRADLE_PUBLISH_KEY' env var or configure 'performPublishPlugins' task."
    }

    def "validates publish secret"() {
        project.plugins.apply(GradlePortalReleasePlugin.class)
        GradlePortalPublishTask t = project.tasks[PERFORM_PUBLISH_TASK]
        t.publishKey = "foo"

        when:
        LazyConfiguration.forceConfiguration(project.tasks[PERFORM_PUBLISH_TASK])

        then:
        def ex = thrown(GradleException)
        ex.message == "Gradle Plugin Portal 'publishSecret' is required. Export 'GRADLE_PUBLISH_SECRET' env var or configure 'performPublishPlugins' task."
    }

    def "sets key based on project property"() {
        project.ext.'gradle.publish.key' = 'abc'
        project.plugins.apply(GradlePortalReleasePlugin.class)
        GradlePortalPublishTask t = project.tasks[PERFORM_PUBLISH_TASK]

        expect:
        t.publishKey == 'abc'
    }

    def "sets secret based on project property"() {
        project.ext.'gradle.publish.secret' = 'shh'
        project.plugins.apply(GradlePortalReleasePlugin.class)
        GradlePortalPublishTask t = project.tasks[PERFORM_PUBLISH_TASK]

        expect:
        t.publishSecret == 'shh'
    }

    def "sets key based on env var"() {
        env.getenv(GradlePortalReleasePlugin.PUBLISH_KEY_ENV) >> "123"
        new GradlePortalReleasePlugin(env).apply(project)
        GradlePortalPublishTask t = project.tasks[PERFORM_PUBLISH_TASK]

        expect:
        t.publishKey == '123'
    }

    def "sets secret based on env var"() {
        env.getenv(GradlePortalReleasePlugin.PUBLISH_SECRET_ENV) >> "ksh"
        new GradlePortalReleasePlugin(env).apply(project)
        GradlePortalPublishTask t = project.tasks[PERFORM_PUBLISH_TASK]

        expect:
        t.publishSecret == 'ksh'
    }

    def "dry run"() {
        project.shipkit.dryRun = true
        project.plugins.apply(GradlePortalReleasePlugin.class)

        expect:
        project.tasks[PERFORM_PUBLISH_TASK].execute()
    }
}

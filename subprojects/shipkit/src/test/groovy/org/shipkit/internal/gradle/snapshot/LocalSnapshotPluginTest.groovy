package org.shipkit.internal.gradle.snapshot

import testutil.PluginSpecification

class LocalSnapshotPluginTest extends PluginSpecification {

    def plugin = new LocalSnapshotPlugin()

    def "configures for snapshot"() {
        def task = project.tasks.create("task")
        def javadoc = project.tasks.create("javadoc")
        def groovydoc = project.tasks.create("groovydoc")

        when:
        def result = LocalSnapshotPlugin.configureTask(task, tasks)

        then:
        result == isSnapshot
        javadoc.enabled == documentationEnabled
        groovydoc.enabled == documentationEnabled

        where:
        tasks               | isSnapshot | documentationEnabled
        ['build']           | false      | true
        ['foo', 'snapshot'] | true       | false
    }
}

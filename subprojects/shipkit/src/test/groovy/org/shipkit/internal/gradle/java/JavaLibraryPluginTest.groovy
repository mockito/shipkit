package org.shipkit.internal.gradle.java

import org.shipkit.internal.gradle.java.tasks.CreateDependencyInfoFileTask
import testutil.PluginSpecification

class JavaLibraryPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(JavaLibraryPlugin)
    }

    def "should configure createDependencyInfoFile"() {
        when:
        project.plugins.apply(JavaLibraryPlugin)
        project.evaluate()

        then:
        CreateDependencyInfoFileTask task = project.tasks.createDependencyInfoFile
        task.configuration == project.configurations.getByName("runtime")
        task.outputFile == new File(project.buildDir, "dependency-info.json")
    }
}

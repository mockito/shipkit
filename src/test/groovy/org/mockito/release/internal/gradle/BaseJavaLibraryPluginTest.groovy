package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.mockito.release.internal.comparison.PublicationsComparatorTask
import org.mockito.release.internal.gradle.configuration.LazyConfiguration
import spock.lang.Specification

class BaseJavaLibraryPluginTest extends Specification {

    def project = new ProjectBuilder().withParent().build()
    @Rule
    def TemporaryFolder tmp = new TemporaryFolder()

    def "applies"() {
        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.base-java-library")
    }

    def "configures comparePublications task correctly"() {
        given:
        def parent = new ProjectBuilder().withName("parent").build()
        def child = new ProjectBuilder().withName("child").withParent(parent).build()

        parent.allprojects.each { p ->
            p.setGroup("org.group")
        }

        when:
        child.plugins.apply("org.mockito.mockito-release-tools.base-java-library")
        def task = (PublicationsComparatorTask) child.getTasks()
                .getByName(BaseJavaLibraryPlugin.COMPARE_PUBLICATIONS_TASK)
        //force lazy configuration so that properties are set
        LazyConfiguration.forceConfiguration(task)

        then:
        task.getProjectGroup() == "org.group"
    }

    def "adds versions to comparePublications task if VersioningPlugin applied on root project"() {
        given:

        def parent = new ProjectBuilder().withProjectDir(tmp.root).withName("parent").build()
        def child = new ProjectBuilder().withName("child").withParent(parent).build()

        tmp.newFile("/version.properties") << "version=0.1.1\npreviousVersion=0.1.0"

        parent.plugins.apply(VersioningPlugin.class)

        when:
        child.plugins.apply("org.mockito.mockito-release-tools.base-java-library")

        then:
        def task = (PublicationsComparatorTask) child.getTasks()
                .getByName(BaseJavaLibraryPlugin.COMPARE_PUBLICATIONS_TASK);

        task.getCurrentVersion() == "0.1.1"
        task.getPreviousVersion() == "0.1.0"
    }
}

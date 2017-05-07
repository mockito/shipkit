package org.mockito.release.internal.gradle

import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.mockito.release.gradle.ReleaseConfiguration
import org.mockito.release.internal.comparison.PublicationsComparatorTask
import org.mockito.release.internal.gradle.configuration.DeferredConfiguration
import spock.lang.Specification

class PublicationsComparatorPluginTest extends Specification {

    @Rule
    def TemporaryFolder tmp = new TemporaryFolder()

    def "applies"() {
        expect:
        new ProjectBuilder().build().plugins.apply("org.mockito.mockito-release-tools.publications-comparator")
    }

    def "configures comparePublications task correctly"() {
        given:
        def parent = new ProjectBuilder().withName("parent").build()
        def child = new ProjectBuilder().withName("child").withParent(parent).build()

        parent.allprojects.each { p ->
            p.setGroup("org.group")
        }

        when:
        child.plugins.apply("org.mockito.mockito-release-tools.publications-comparator")
        DeferredConfiguration.forceConfiguration(child)

        then:
        def task = (PublicationsComparatorTask) child.getTasks()
                .getByName(PublicationsComparatorPlugin.COMPARE_PUBLICATIONS_TASK)
        task.getProjectGroup() == "org.group"
    }

    def "adds versions to comparePublications task if VersioningPlugin applied on root project"() {
        given:

        def parent = new ProjectBuilder().withProjectDir(tmp.root).withName("parent").build()
        def child = new ProjectBuilder().withName("child").withParent(parent).build()

        tmp.newFile("/version.properties") << "version=0.1.1\npreviousVersion=0.1.0"

        parent.plugins.apply(VersioningPlugin)

        when:
        child.plugins.apply("org.mockito.mockito-release-tools.publications-comparator")

        then:
        def task = (PublicationsComparatorTask) child.getTasks()
                .getByName(PublicationsComparatorPlugin.COMPARE_PUBLICATIONS_TASK);

        task.getCurrentVersion() == "0.1.1"
        task.getPreviousVersion() == "0.1.0"
    }

    def "sets DefaultUrlResolver to Bintray if BintrayPlugin is applied"() {
        given:

        def parent = new ProjectBuilder().withProjectDir(tmp.root).withName("parent").build()
        def child = new ProjectBuilder().withName("child").withParent(parent).build()

        child.plugins.apply(BintrayPlugin)
        child.getExtensions().getByType(BintrayExtension).user = "test";

        def releaseConfig = parent.getExtensions().getByType(ReleaseConfiguration)
        releaseConfig.gitHub.repository = "repo"

        when:
        child.plugins.apply("org.mockito.mockito-release-tools.publications-comparator")
        DeferredConfiguration.forceConfiguration(child)

        then:
        def task = (PublicationsComparatorTask) child.getTasks()
                .getByName(PublicationsComparatorPlugin.COMPARE_PUBLICATIONS_TASK);

        task.defaultArtifactUrlResolver.class.simpleName == "BintrayDefaultArtifactUrlResolver"
    }
}

package org.shipkit.internal.gradle.release

import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.gradle.ReleaseConfiguration
import org.shipkit.internal.comparison.DownloadPreviousReleaseArtifactsTask
import org.shipkit.internal.comparison.PublicationsComparatorTask
import org.shipkit.internal.gradle.ShipkitBintrayPlugin
import org.shipkit.internal.gradle.VersioningPlugin
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin
import testutil.PluginSpecification

class PublicationsComparatorPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply("org.shipkit.publications-comparator")
    }

    def "configures comparePublications task correctly"() {
        given:
        def parent = new ProjectBuilder().withProjectDir(tmp.root).withName("parent").build()
        def child = new ProjectBuilder().withName("child").withParent(parent).build()

        new File(tmp.root.absolutePath + "/version.properties") << "version=1.0.1\npreviousVersion=1.0.0"

        parent.allprojects{
            group = "org.group"
        }

        when:
        child.plugins.apply("org.shipkit.publications-comparator")
        child.evaluate()

        then:
        PublicationsComparatorTask task = child.getTasks().getByName(PublicationsComparatorPlugin.COMPARE_PUBLICATIONS_TASK)
        task.getProjectGroup() == "org.group"
        task.getCurrentVersion() == "1.0.1"
        task.getPreviousVersion() == "1.0.0"
    }

    def "adds versions to comparePublications task if VersioningPlugin applied on root project"() {
        given:

        def parent = new ProjectBuilder().withProjectDir(tmp.root).withName("parent").build()
        def child = new ProjectBuilder().withName("child").withParent(parent).build()

        tmp.newFile("/version.properties") << "version=0.1.1\npreviousVersion=0.1.0"

        parent.plugins.apply(VersioningPlugin)

        when:
        child.plugins.apply("org.shipkit.publications-comparator")

        then:
        PublicationsComparatorTask task = child.getTasks().getByName(PublicationsComparatorPlugin.COMPARE_PUBLICATIONS_TASK);

        task.getCurrentVersion() == "0.1.1"
        task.getPreviousVersion() == "0.1.0"
    }

    def "sets previousVersionPomLocalFile and previousVersionSourcesJarLocalFile to Bintray defaults if BintrayPlugin is applied"() {
        given:
        def parent = new ProjectBuilder().withProjectDir(tmp.root).withName("parent").build()
        def child = new ProjectBuilder().withName("child").withParent(parent).build()

        child.plugins.apply(ShipkitBintrayPlugin)
        child.getExtensions().getByType(BintrayExtension).user = "test";

        def releaseConfig = parent.getExtensions().getByType(ReleaseConfiguration)
        releaseConfig.gitHub.repository = "repo"

        when:
        child.plugins.apply("org.shipkit.publications-comparator")
        child.evaluate()

        then:
        DownloadPreviousReleaseArtifactsTask task = child.getTasks()
                .getByName(PublicationsComparatorPlugin.DOWNLOAD_PREVIOUS_RELEASE_ARTIFACTS_TASK);

        task.previousVersionPomUrl.contains("bintray.com")
        task.previousVersionSourcesJarUrl.contains("bintray.com")
    }

    def "leaves previousVersionPomLocalFile and previousVersionSourcesJarLocalFile null if BintrayPlugin is NOT applied"() {
        given:
        def parent = new ProjectBuilder().withProjectDir(tmp.root).withName("parent").build()
        def child = new ProjectBuilder().withName("child").withParent(parent).build()

        when:
        child.plugins.apply("org.shipkit.publications-comparator")
        child.evaluate()

        then:
        DownloadPreviousReleaseArtifactsTask task = child.getTasks()
                .getByName(PublicationsComparatorPlugin.DOWNLOAD_PREVIOUS_RELEASE_ARTIFACTS_TASK);

        task.previousVersionPomUrl == null
        task.previousVersionSourcesJarUrl == null
    }

    def "sets correctly local files in download and comparison tasks"() {
        given:
        def parent = new ProjectBuilder().withProjectDir(tmp.root).withName("parent").build()
        def child = new ProjectBuilder().withName("child").withParent(parent).build()

        child.plugins.apply(ReleaseConfigurationPlugin)
        def conf = parent.getExtensions().getByType(ReleaseConfiguration)
        conf.setPreviousReleaseVersion("1.0.0")

        when:
        child.plugins.apply("org.shipkit.publications-comparator")
        child.evaluate()

        then:
        DownloadPreviousReleaseArtifactsTask downloadTask = child.getTasks()
                .getByName(PublicationsComparatorPlugin.DOWNLOAD_PREVIOUS_RELEASE_ARTIFACTS_TASK)
        PublicationsComparatorTask comparisonTask = child.getTasks()
                .getByName(PublicationsComparatorPlugin.COMPARE_PUBLICATIONS_TASK)

        def basePath = child.getBuildDir().absolutePath + "/previous-release-artifacts/child-1.0.0";
        def expectedPom = new File(basePath + ".pom")
        def expectedSourcesJar = new File(basePath + "-sources.jar")

        downloadTask.previousVersionPomLocalFile == expectedPom
        downloadTask.previousVersionSourcesJarLocalFile == expectedSourcesJar

        comparisonTask.previousVersionPomFile == expectedPom
        comparisonTask.previousVersionSourcesJarFile == expectedSourcesJar
    }
}

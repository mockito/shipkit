package org.shipkit.internal.gradle.java

import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.gradle.ComparePublicationsTask
import org.shipkit.gradle.ReleaseConfiguration
import org.shipkit.internal.comparison.DownloadPreviousPublicationsTask
import org.shipkit.internal.gradle.ShipkitBintrayPlugin
import org.shipkit.internal.gradle.VersioningPlugin
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin
import testutil.PluginSpecification

class ComparePublicationsPluginTest extends PluginSpecification {

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
        ComparePublicationsTask task = child.getTasks().getByName(ComparePublicationsPlugin.COMPARE_PUBLICATIONS_TASK)
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
        ComparePublicationsTask task = child.getTasks().getByName(ComparePublicationsPlugin.COMPARE_PUBLICATIONS_TASK);

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
        DownloadPreviousPublicationsTask task = child.getTasks()
                .getByName(ComparePublicationsPlugin.DOWNLOAD_PREVIOUS_RELEASE_ARTIFACTS_TASK);

        task.previousPomUrl.contains("bintray.com")
        task.previousSourcesJarUrl.contains("bintray.com")
    }

    def "leaves previousVersionPomLocalFile and previousVersionSourcesJarLocalFile null if BintrayPlugin is NOT applied"() {
        given:
        def parent = new ProjectBuilder().withProjectDir(tmp.root).withName("parent").build()
        def child = new ProjectBuilder().withName("child").withParent(parent).build()

        when:
        child.plugins.apply("org.shipkit.publications-comparator")
        child.evaluate()

        then:
        DownloadPreviousPublicationsTask task = child.getTasks()
                .getByName(ComparePublicationsPlugin.DOWNLOAD_PREVIOUS_RELEASE_ARTIFACTS_TASK);

        task.previousPomUrl == null
        task.previousSourcesJarUrl == null
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
        DownloadPreviousPublicationsTask downloadTask = child.getTasks()
                .getByName(ComparePublicationsPlugin.DOWNLOAD_PREVIOUS_RELEASE_ARTIFACTS_TASK)
        ComparePublicationsTask comparisonTask = child.getTasks()
                .getByName(ComparePublicationsPlugin.COMPARE_PUBLICATIONS_TASK)

        def basePath = child.getBuildDir().absolutePath + "/previous-release-artifacts/child-1.0.0";
        def expectedPom = new File(basePath + ".pom")
        def expectedSourcesJar = new File(basePath + "-sources.jar")

        downloadTask.previousPom == expectedPom
        downloadTask.previousSourcesJar == expectedSourcesJar

        comparisonTask.previousVersionPomFile == expectedPom
        comparisonTask.previousVersionSourcesJarFile == expectedSourcesJar
    }
}

package org.shipkit.internal.gradle

import org.gradle.api.GradleException
import org.shipkit.internal.gradle.bintray.ShipkitBintrayPlugin
import org.shipkit.internal.gradle.configuration.LazyConfiguration
import testutil.PluginSpecification

class ShipkitBintrayPluginTest extends PluginSpecification {

    def setup() {
        project.plugins.apply("org.shipkit.bintray")
    }

    def "deferred configuration"() {
        project.version = "1.0"
        project.group = "org.shipkit"
        project.description = "some proj"
        project.plugins.apply("org.shipkit.bintray")

        project.shipkit.dryRun = true
        project.shipkit.gitHub.repository = 'repo'
        project.bintray.user = 'szczepiq'

        when:
        project.evaluate()

        then:
        project.bintray.pkg.version.vcsTag == "v1.0"
        project.bintray.dryRun == true
        project.bintray.pkg.vcsUrl == "https://github.com/repo.git"
        project.bintray.pkg.issueTrackerUrl == "https://github.com/repo/issues"
        project.bintray.pkg.websiteUrl == "https://github.com/repo"
        project.bintray.pkg.desc == "some proj"
        project.bintray.pkg.name == "org.shipkit"
    }

    def "deferred configuration honors user settings"() {
        project.version = "1.0"
        project.group = "org.shipkit"
        project.description = "some proj"
        project.plugins.apply("org.shipkit.bintray")

        project.shipkit.dryRun = true
        project.shipkit.gitHub.repository = 'repo'

        project.bintray.dryRun = false //this one is not honored at the moment, we're ok with this
        project.bintray.user = 'szczepiq'
        project.bintray.key = 'xyz'
        project.bintray.pkg.vcsUrl = "vcs"
        project.bintray.pkg.version.vcsTag = "v4.0"
        project.bintray.pkg.issueTrackerUrl = "issueTracker"
        project.bintray.pkg.websiteUrl = "website"
        project.bintray.pkg.desc = "my desc"
        project.bintray.pkg.name = "my name"

        when:
        project.evaluate()
        LazyConfiguration.forceConfiguration(project.tasks.bintrayUpload)

        then:
        project.bintray.dryRun == true
        project.bintray.key == 'xyz'
        project.bintray.pkg.vcsUrl == "vcs"
        project.bintray.pkg.issueTrackerUrl == "issueTracker"
        project.bintray.pkg.websiteUrl == "website"
        project.bintray.pkg.desc == "my desc"
        project.bintray.pkg.name == "my name"
        project.bintray.pkg.version.vcsTag == "v4.0"
    }

    def "fails if bintray.user is not set"() {
        project.plugins.apply("org.shipkit.bintray")

        project.bintray.key = 'xyz'

        when:
        project.evaluate()
        LazyConfiguration.forceConfiguration(project.tasks.bintrayUpload)

        then:
        def ex = thrown(GradleException)
        ex.message ==
                "Missing 'bintray.user' value.\n" +
                "  Please configure Bintray extension."
    }

    def "prints informative message before upload"() {
        //BintrayUploadTask cannot be referenced directly due to runtime error related to changes between Gradle 2 and 3:
        //java.lang.RuntimeException: java.lang.NoClassDefFoundError: Unable to load class com.jfrog.bintray.gradle.BintrayUploadTask due to missing dependency org/gradle/api/internal/DynamicObject
        def u = project.tasks[ShipkitBintrayPlugin.BINTRAY_UPLOAD_TASK]
        u.versionName = "1.0.0"
        u.user = "shipkit-bot"
        u.userOrg = "shipkit.org"
        u.repoName = "shipkit"
        u.packageName = "shipkit-example"

        expect:
        ShipkitBintrayPlugin.uploadWelcomeMessage(u) == """:bintrayUpload - publishing to Bintray
  - dry run: false, version: 1.0.0, Maven Central sync: false
  - user/org: shipkit-bot/shipkit.org, repository/package: shipkit/shipkit-example"""
    }
}

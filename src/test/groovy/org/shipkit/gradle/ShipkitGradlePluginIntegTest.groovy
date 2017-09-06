package org.shipkit.gradle

import testutil.GradleSpecification

class ShipkitGradlePluginIntegTest extends GradleSpecification {

    def "all tasks in dry run"() {
        projectDir.newFolder("gradle")
        projectDir.newFile("gradle/shipkit.gradle") << """
            shipkit {
                gitHub.readOnlyAuthToken = "foo"
                gitHub.repository = "repo"
            }
        """

        buildFile << """
            apply plugin: "org.shipkit.gradle-plugin"
            apply plugin: "com.gradle.plugin-publish"
            ext.'gradle.publish.key' = 'key'
            ext.'gradle.publish.secret' = 'secret'
        """

        projectDir.newFile("version.properties") << "version=1.0.0"

        expect:
        def result = pass("performRelease", "-m", "-s")
        result.tasks.join("\n") == """:bumpVersionFile=SKIPPED
:fetchContributors=SKIPPED
:fetchReleaseNotes=SKIPPED
:updateReleaseNotes=SKIPPED
:gitCommit=SKIPPED
:compileJava=SKIPPED
:processResources=SKIPPED
:classes=SKIPPED
:jar=SKIPPED
:publishPluginJar=SKIPPED
:javadoc=SKIPPED
:publishPluginJavaDocsJar=SKIPPED
:buildArchives=SKIPPED
:gitTag=SKIPPED
:identifyGitBranch=SKIPPED
:gitPush=SKIPPED
:performGitPush=SKIPPED
:discoverPlugins=SKIPPED
:publishPlugins=SKIPPED
:performRelease=SKIPPED"""
    }
}

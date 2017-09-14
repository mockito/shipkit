package org.shipkit.gradle

import org.gradle.testkit.runner.BuildResult
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
        BuildResult result = pass("performRelease", "-m", "-s")
        skippedTaskPathsGradleBugWorkaround(result.output).join("\n") == """:bumpVersionFile
:fetchContributors
:fetchReleaseNotes
:updateReleaseNotes
:gitCommit
:compileJava
:processResources
:classes
:jar
:publishPluginJar
:javadoc
:publishPluginJavaDocsJar
:buildArchives
:gitTag
:identifyGitBranch
:identifyGitOrigin
:gitPush
:performGitPush
:discoverPlugins
:publishPlugins
:performRelease"""
    }
}

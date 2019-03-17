package org.shipkit.gradle

import org.gradle.testkit.runner.BuildResult
import testutil.GradleSpecification

class ShipkitJavadocIntegTest extends GradleSpecification {

    def "all tasks in dry run (gradle #gradleVersionToTest)"() {
        given:
        gradleVersion = gradleVersionToTest

        and:
        newFile("gradle/shipkit.gradle") << """
            shipkit {
                gitHub.readOnlyAuthToken = "foo"
                gitHub.writeAuthToken = "secret"
                releaseNotes.file = "CHANGELOG.md"
                git.user = "shipkit"
                git.email = "shipkit.org@gmail.com"
                gitHub.repository = "repo"
            }

            allprojects {
                plugins.withId("org.shipkit.bintray") {
                    bintray {
                        user = "szczepiq"
                        key = "secret"
                    }
                }
            }
        """

        buildFile << """
        apply plugin: 'org.shipkit.java'
        apply plugin: 'org.shipkit.javadoc'
        """

        settingsFile << "include 'api', 'impl'"
        newFile('api/build.gradle') << "apply plugin: 'java'"
        newFile('impl/build.gradle') << "apply plugin: 'java'"

        expect:
        BuildResult result = pass("performRelease", "-m", "-s")
        //git push and bintray upload tasks should run as late as possible
        skippedTaskPathsGradleBugWorkaround(result.output).join("\n") == """:bumpVersionFile
:identifyGitBranch
:fetchContributors
:fetchReleaseNotes
:updateReleaseNotes
:gitCommit
:gitTag
:api:generatePomFileForJavaLibraryPublication
:api:compileJava
:api:processResources
:api:classes
:api:jar
:api:javadoc
:api:javadocJar
:api:createDependencyInfoFile
:api:sourcesJar
:api:publishJavaLibraryPublicationToMavenLocal
:impl:generatePomFileForJavaLibraryPublication
:impl:compileJava
:impl:processResources
:impl:classes
:impl:jar
:impl:javadoc
:impl:javadocJar
:impl:createDependencyInfoFile
:impl:sourcesJar
:impl:publishJavaLibraryPublicationToMavenLocal
:gitPush
:performGitPush
:api:copyJavadocToStageVersionDir
:impl:copyJavadocToStageVersionDir
:refreshVersionJavadoc
:cloneJavadocRepo
:checkoutJavadocRepoBranch
:api:copyJavadocToStageCurrentDir
:impl:copyJavadocToStageCurrentDir
:refreshCurrentJavadoc
:copyJavadocStageToRepoDir
:commitJavadoc
:pushJavadoc
:releaseJavadoc
:updateReleaseNotesOnGitHub
:api:bintrayUpload
:impl:bintrayUpload
:bintrayPublish
:performRelease"""

        where:
        gradleVersionToTest << determineGradleVersionsToTest()
    }
}

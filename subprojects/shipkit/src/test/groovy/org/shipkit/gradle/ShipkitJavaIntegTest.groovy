package org.shipkit.gradle

import org.gradle.testkit.runner.BuildResult
import testutil.GradleSpecification

class ShipkitJavaIntegTest extends GradleSpecification {

    def "all tasks in dry run"() {

        /**
         * TODO this test is just a starting point we will make it better and create more integration tests
         * Stuff that we should do:
         *  1. (Most important) Avoid writing too many integration tests. Most code should be covered by unit tests (see testing pyramid)
         *  2. Push out complexity to base class GradleSpecification
         *      so that what remains in the test is the essential part of a tested feature
         *  3. Add more specific assertions rather than just a list of tasks in dry run mode
         *  4. Use sensible defaults so that we don't need to specify all configuration in the test
         *  5. Move integration tests to a separate module
         *  6. Dependencies are hardcoded between GradleSpecification and build.gradle of release-tools project
         */

        projectDir.newFolder("gradle")
        projectDir.newFile("gradle/shipkit.gradle") << """
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
            apply plugin: "org.shipkit.java"
        """

        settingsFile << "include 'api', 'impl'"
        projectDir.newFile("version.properties") << "version=1.0.0"
        projectDir.newFolder('api')
        projectDir.newFolder('impl')
        projectDir.newFile('api/build.gradle') << "apply plugin: 'java'"
        projectDir.newFile('impl/build.gradle') << "apply plugin: 'java'"

        expect:
        BuildResult result = pass("performRelease", "-m", "-s")
        //git push and bintray upload tasks should run as late as possible
        skippedTaskPathsGradleBugWorkaround(result.output).join("\n") == """:bumpVersionFile
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
:api:sourcesJar
:api:publishJavaLibraryPublicationToMavenLocal
:impl:generatePomFileForJavaLibraryPublication
:impl:compileJava
:impl:processResources
:impl:classes
:impl:jar
:impl:javadoc
:impl:javadocJar
:impl:sourcesJar
:impl:publishJavaLibraryPublicationToMavenLocal
:identifyGitBranch
:identifyGitOrigin
:gitPush
:performGitPush
:api:bintrayUpload
:impl:bintrayUpload
:performRelease"""
    }
}

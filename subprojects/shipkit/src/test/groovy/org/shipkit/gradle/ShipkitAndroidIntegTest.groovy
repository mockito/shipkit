package org.shipkit.gradle

import org.gradle.testkit.runner.BuildResult
import testutil.GradleSpecification

class ShipkitAndroidIntegTest extends GradleSpecification {

    def "all tasks in dry run (gradle #gradleVersionToTest)"() {
        /**
         * TODO this test is just a starting point we will make it better and create more integration tests
         * Stuff that we should do:
         *  1. (Most important) Avoid writing too many integration tests. Most code should be covered by unit tests
         *      (see testing pyramid)
         *  2. Push out complexity to base class GradleSpecification
         *      so that what remains in the test is the essential part of a tested feature
         *  3. Add more specific assertions rather than just a list of tasks in dry run mode
         *  4. Use sensible defaults so that we don't need to specify all configuration in the test
         *  5. Move integration tests to a separate module
         *  6. Dependencies are hardcoded between GradleSpecification and build.gradle of release-tools project
         */
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
                android.artifactId = "shipkit-android"
            }

            allprojects {
                plugins.withId("com.jfrog.bintray") {
                    bintray {
                        user = "szczepiq"
                        key = "secret"
                    }
                }
            }
        """
        newFile("src/main/AndroidManifest.xml") << """<manifest package="org.shipkit.android"/>"""

        buildFile << """
            apply plugin: 'org.shipkit.java'
            buildscript {
                repositories {
                    google()
                    jcenter()
                    gradlePluginPortal()
                }
                dependencies {
                    classpath 'com.github.technoir42:aar-publish-plugin:1.0.2'
                    classpath 'com.android.tools.build:gradle:3.4.1'
                }
            }
        """

        settingsFile << "include 'lib'"
        newFile('lib/build.gradle') << """
            apply plugin: 'org.shipkit.bintray'
            apply plugin: 'org.shipkit.android-publish'
            apply plugin: 'com.android.library'
            android {
                compileSdkVersion 28
                defaultConfig {
                    minSdkVersion 28
                }
            }
        """

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
:gitPush
:performGitPush
:updateReleaseNotesOnGitHub
:lib:preBuild
:lib:preReleaseBuild
:lib:compileReleaseAidl
:lib:compileReleaseRenderscript
:lib:checkReleaseManifest
:lib:generateReleaseBuildConfig
:lib:generateReleaseResValues
:lib:generateReleaseResources
:lib:packageReleaseResources
:lib:processReleaseManifest
:lib:generateReleaseRFile
:lib:prepareLintJar
:lib:generateReleaseSources
:lib:javaPreCompileRelease
:lib:compileReleaseJavaWithJavac
:lib:extractReleaseAnnotations
:lib:mergeReleaseConsumerProguardFiles
:lib:mergeReleaseShaders
:lib:compileReleaseShaders
:lib:generateReleaseAssets
:lib:packageReleaseAssets
:lib:packageReleaseRenderscript
:lib:prepareLintJarForPublish
:lib:processReleaseJavaRes
:lib:transformResourcesWithMergeJavaResForRelease
:lib:transformClassesAndResourcesWithSyncLibJarsForRelease
:lib:mergeReleaseJniLibFolders
:lib:transformNativeLibsWithMergeJniLibsForRelease
:lib:transformNativeLibsWithSyncJniLibsForRelease
:lib:bundleReleaseAar
:lib:generatePomFileForJavaLibraryPublication
:lib:javadocRelease
:lib:packageReleaseJavadoc
:lib:packageReleaseSources
:lib:publishJavaLibraryPublicationToMavenLocal
:lib:bintrayUpload
:bintrayPublish
:performRelease"""

        where:
        gradleVersionToTest << ["5.3", "5.4.1"]
    }

    def "fails on unsupported Gradle version #gradleVersionToTest"() {
        given:
        gradleVersion = gradleVersionToTest

        settingsFile << "include 'lib'"
        newFile('lib/build.gradle') << """
            apply plugin: 'org.shipkit.android-publish'
        """

        expect:
        BuildResult result = fail("performRelease", "-m", "-s")
        result.output.contains("Current Gradle version: " + gradleVersionToTest + " is less than minimum required: 5.3")

        where:
        gradleVersionToTest << ["5.0", "4.10.3", "4.0.2"]
    }
}

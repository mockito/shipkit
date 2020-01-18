package org.shipkit.gradle

import org.gradle.testkit.runner.BuildResult
import testutil.GradleSpecification

class ShipkitAndroidIntegTest extends GradleSpecification {

    void setup() {
        settingsFile << "include 'lib'"
        newFile('lib/build.gradle') << """
            apply plugin: 'org.shipkit.bintray'
            apply plugin: 'org.shipkit.android-publish'
            androidPublish {
                artifactId = 'shipkit-android'
            }

            apply plugin: 'com.android.library'
            android {
                compileSdkVersion 29
                defaultConfig {
                    minSdkVersion 29
                }
            }
        """

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
                plugins.withId("com.jfrog.bintray") {
                    bintray {
                        user = "szczepiq"
                        key = "secret"
                    }
                }
            }
        """
        buildFile << """
            apply plugin: 'org.shipkit.java'
            buildscript {
                repositories {
                    google()
                    jcenter()
                    gradlePluginPortal()
                }
            }
        """
        newFile("src/main/AndroidManifest.xml") << """<manifest package="org.shipkit.android"/>"""
    }

    def "all tasks in dry run (gradle #gradleVersionToTest) (AGP #agpVersionToTest)"() {
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
        buildFile << """
            buildscript {
                dependencies {
                    classpath 'com.android.tools.build:gradle:$agpVersionToTest'
                }
            }
        """

        expect:
        BuildResult result = pass("performRelease", "-m", "-s")
        //git push and bintray upload tasks should run as late as possible
        def output = skippedTaskPathsGradleBugWorkaround(result.output).join("\n")
        output.startsWith(""":bumpVersionFile
:identifyGitBranch
:fetchContributors
:fetchReleaseNotes
:updateReleaseNotes
:gitCommit
:gitTag
:gitPush
:performGitPush
:updateReleaseNotesOnGitHub
:lib:preBuild""")

        and:
        output.endsWith(""":lib:bintrayUpload
:bintrayPublish
:performRelease""")

        where:
        gradleVersionToTest << ["5.6.4", "6.0.1"]
        and:
        agpVersionToTest << ["3.6.0-beta05", "3.6.0-rc01"]
    }

    def "fails on unsupported dependency versions (gradle #gradleVersionToTest) (AGP #agpVersionToTest)"() {
        given:
        gradleVersion = gradleVersionToTest

        and:
        buildFile << """
            buildscript {
                dependencies {
                    classpath 'com.android.tools.build:gradle:$agpVersionToTest'
                }
            }
        """

        expect:
        BuildResult result = fail("performRelease", "-m", "-s")
        result.output.contains("'release' component not found in project. " +
            "Make sure you are using Android Gradle Plugin 3.6.0-beta05 or newer.")

        where:
        gradleVersionToTest << ["5.6.4", "6.0.1"]
        and:
        agpVersionToTest << ["3.4.0", "3.5.2"]
    }
}

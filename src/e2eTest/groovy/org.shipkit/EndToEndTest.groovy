package org.shipkit.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.internal.gradle.ShipkitJavaPlugin
import spock.lang.Specification

class EndToEndTest extends Specification{

    public static final String GRADLE_VERSION = "3.5"
    @Rule
    final TemporaryFolder projectDir = new TemporaryFolder()

    File buildFile

    private static final String MAIN_CLASSES_DIR = findMainClassesDir();

    def "should clone a project and run performRelease on it without failure"() {
        given:
        buildFile = projectDir.newFile('build.gradle')
        buildFile << """buildscript {
                dependencies {
                    classpath files("${MAIN_CLASSES_DIR}")
                }
            }
            import org.shipkit.internal.gradle.E2ETestingPlugin
            apply plugin: 'base'
            apply plugin: E2ETestingPlugin

            e2eTest.create("https://github.com/mockito/shipkit-example")
        """

        expect:
        GradleRunner.create()
                .withProjectDir(projectDir.root)
                .withArguments("cloneProjectToWorkDirShipkit-example", "-s")
                .withDebug(true)
                .build()


        def result2 = GradleRunner.create()
                .withProjectDir(new File(projectDir.root.absolutePath + "/build/shipkit-example-work"))
                .withGradleVersion(GRADLE_VERSION)
                .withArguments("publishToMavenLocal", "performRelease",
                            "-x", "gitPush", "-x", "bintrayUpload",
                            "--include-build", new File("").getAbsolutePath(), "-i")
                .build()


        def expected = [
            ":shipkit:compileJava",
            ":shipkit:compileGroovy",
            ":shipkit:processResources",
            ":shipkit:classes",
            ":shipkit:jar",
            ":fetchAllContributors",
            ":api:generatePomFileForJavaLibraryPublication",
            ":api:compileJava",
            ":api:processResources",
            ":api:classes",
            ":api:jar",
            ":api:javadoc",
            ":api:javadocJar",
            ":api:sourcesJar",
            ":api:publishJavaLibraryPublicationToMavenLocal",
            ":api:publishToMavenLocal",
            ":impl:generatePomFileForJavaLibraryPublication",
            ":impl:compileJava",
            ":impl:processResources",
            ":impl:classes",
            ":impl:jar",
            ":impl:javadoc",
            ":impl:javadocJar",
            ":impl:sourcesJar",
            ":impl:publishJavaLibraryPublicationToMavenLocal",
            ":impl:publishToMavenLocal",
            ":bumpVersionFile",
            ":fetchReleaseNotes",
            ":updateReleaseNotes",
            ":gitCommit",
            ":gitTag",
            ":performGitPush",
            ":performRelease"
        ]

        result2.tasks*.path == expected
        result2.tasks*.outcome != TaskOutcome.FAILED
    }

    private static String findMainClassesDir() {
        //Using one of the production classes to find directory where IDE or the build system outputs compiled production code
        def bearing = ShipkitJavaPlugin.class.name.replaceAll("\\.", "/") + ".class"
        return findDir(bearing)
    }

    private static String findDir(String bearing) {
        //Based on a bearing resource, we're finding root directory in the classpath
        def gradlePluginsDir = EndToEndTest.classLoader.getResource(bearing).file
        def classesDir = gradlePluginsDir - bearing
        assert new File(classesDir).isDirectory()
        return classesDir
    }
}

package testutil

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GradleVersion
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.internal.gradle.java.ShipkitJavaPlugin
import spock.lang.Specification

/**
 * Allows convenient testing of Gradle plugins:
 *  - automated addition of the classpath without any extra Gradle tasks in project's build.gradle file
 *  - convenience methods for running / failing tasks and printing the build.gradle file contents
 */
abstract class GradleSpecification extends Specification implements GradleVersionsDeterminer {

    @Rule
    final TemporaryFolder projectDir = new TemporaryFolder()

    File buildFile
    File settingsFile
    boolean debug
    String gradleVersion = GradleVersion.current().version

    private static final String CLASSES_DIR = findClassesDir();
    private static final String RESOURCES_DIR = findResourcesDir();

    void setup() {
        buildFile = projectDir.newFile('build.gradle')
        buildFile << """buildscript {
            dependencies {
                classpath files("${CLASSES_DIR}")
                classpath files("${RESOURCES_DIR}")
                classpath "com.github.cliftonlabs:json-simple:2.1.2"
                classpath "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.3"
                classpath "com.gradle.publish:plugin-publish-plugin:0.9.6"
            }
            
            repositories {
                jcenter()
                maven { url "https://plugins.gradle.org/m2/" }
            }
        }"""
        settingsFile = projectDir.newFile('settings.gradle')
    }

    /**
     * Runs Gradle with given arguments, prints the build.gradle file to the standard output if the test fails
     */
    protected BuildResult pass(String... args) {
        try {
            createPreConfiguredGradleRunnerForArgs(args)
                .build()
        } catch (Exception e) {
            println " ---- build.gradle ---- \n" + buildFile.text + "\n ------------------------"
            throw e
        }
    }

    private GradleRunner createPreConfiguredGradleRunnerForArgs(String... args) {
        return GradleRunner.create()
            .withProjectDir(projectDir.root)
            .withArguments(args)
            .withGradleVersion(gradleVersion)
            .withDebug(debug)
    }

    /**
     * Runs Gradle with given arguments, expects it to FAIL,
     * prints the build.gradle file to the standard output if the test fails
     */
    protected BuildResult fail(String... args) {
        try {
            createPreConfiguredGradleRunnerForArgs(args)
                .buildAndFail()
        } catch (Exception e) {
            println " ---- build.gradle ---- \n" + buildFile.text + "\n ------------------------"
            throw e
        }
    }

    protected List<String> skippedTaskPathsGradleBugWorkaround(String output) {
        //Due to https://github.com/gradle/gradle/issues/2732 no tasks are returned in dry-run mode. When fixed ".taskPaths(SKIPPED)" should be used directly
        return output.readLines().findAll { it.endsWith(" SKIPPED") }.collect { it.substring(0, it.lastIndexOf(" "))}
    }

    private static String findClassesDir() {
        //Using one of the production classes to find directory where IDE or the build system outputs compiled production code
        def bearing = ShipkitJavaPlugin.class.name.replaceAll("\\.", "/") + ".class"
        return findDir(bearing)
    }

    private static String findResourcesDir() {
        //Using standard location of gradle plugins to find where production resources are outputted by IDE or build system
        def bearing = "META-INF/gradle-plugins"
        return findDir(bearing)
    }

    private static String findDir(String bearing) {
        //Based on a bearing resource, we're finding root directory in the classpath
        def gradlePluginsDir = GradleSpecification.classLoader.getResource(bearing).file
        def classesDir = gradlePluginsDir - bearing
        assert new File(classesDir).isDirectory()
        return classesDir
    }
}

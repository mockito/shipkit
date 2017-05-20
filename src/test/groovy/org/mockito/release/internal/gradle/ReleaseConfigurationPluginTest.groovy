package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class ReleaseConfigurationPluginTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def root
    def subproject

    void setup(){
        root = new ProjectBuilder().withProjectDir(tmp.root).build()
        subproject = new ProjectBuilder().withParent(root).build()
    }

    def "singleton configuration, root applied first"() {
        expect:
        root.plugins.apply(ReleaseConfigurationPlugin).configuration == subproject.plugins.apply(ReleaseConfigurationPlugin).configuration
    }

    def "singleton configuration, subproject applied first"() {
        expect:
        subproject.plugins.apply(ReleaseConfigurationPlugin).configuration == root.plugins.apply(ReleaseConfigurationPlugin).configuration
    }

    def "dry run on by default"() {
        expect:
        root.plugins.apply(ReleaseConfigurationPlugin).configuration.dryRun
    }

    @Unroll
    def "configures dry run to #setting when project property is #property"() {
        when:
        root.ext.'releasing.dryRun' = property

        then:
        root.plugins.apply(ReleaseConfigurationPlugin).configuration.dryRun == setting

        where:
        property | setting
        "false"  | false
        "true"   | true
        ""       | true
        null     | true
    }

    def "knows if the release is not notable"() {
        def conf = root.plugins.apply(ReleaseConfigurationPlugin).configuration

        expect: !conf.notableRelease

        when: conf.notableRelease = true
        then: conf.notableRelease
    }

    def "knows if the release is notable"() {
        root.file("version.properties") << "version=1.5.0"

        expect:
        root.plugins.apply(ReleaseConfigurationPlugin).configuration.notableRelease
    }

    def "creates shitpkit.gradle file if it doesn't exist"() {
        expect:
        root.plugins.apply(ReleaseConfigurationPlugin)

        new File(tmp.root.absolutePath + "/gradle/shipkit.gradle").text ==
                "releasing {\n" +
                    "\tgitHub.repository = \"mockito/mockito\"\n" +
                    "\tgitHub.writeAuthUser = \"wwilk\"\n" +
                    "\tgitHub.writeAuthToken = System.getenv(\"GH_WRITE_TOKEN\")\n" +
                    "\tgitHub.readOnlyAuthToken = \"e7fe8fcfd6ffedac384c8c4c71b2a48e646ed1ab\"\n" +
                    "\tgit.user = \"Mockito Release Tools\"\n" +
                    "\tgit.email = \"<mockito.release.tools@gmail.com>\"\n" +
                    "\tgit.releasableBranchRegex = \"master|release/.+\"\n" +
                    "\treleaseNotes.file = \"docs/release-notes.md\"\n" +
                    "\treleaseNotes.notableFile = \"docs/notable-release-notes.md\"\n" +
                    "\treleaseNotes.labelMapping = [noteworthy:\"Noteworthy\",bugfix:\"Bugfixes\"]\n" +
                    "\tteam.developers = [\"szczepiq:Szczepan Faber\"]\n" +
                    "\tteam.contributors = [\"mstachniuk:Marcin Stachniuk\",\"wwilk:Wojtek Wilk\"]\n" +
                "}\n"
    }
}

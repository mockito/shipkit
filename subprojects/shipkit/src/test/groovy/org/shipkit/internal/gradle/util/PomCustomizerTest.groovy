package org.shipkit.internal.gradle.util

import org.gradle.api.publish.maven.MavenPublication
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.gradle.configuration.ShipkitConfiguration
import org.shipkit.internal.notes.contributors.DefaultProjectContributor
import org.shipkit.internal.notes.contributors.DefaultProjectContributorsSet
import spock.lang.Specification

class PomCustomizerTest extends Specification {

    def "registers xml action to customize pom"() {
        def project = new ProjectBuilder().build()

        when:
        project.plugins.apply("java")
        project.plugins.apply("maven-publish")

        then:
        project.publishing.publications {
            mainJar(MavenPublication) {
                from project.components.java
                PomCustomizer.customizePom(project, null, it)
            }
        }
    }

    def node = new Node(null, "project")
    def conf = new ShipkitConfiguration()

    def "pom contributors from settings"() {
        conf.gitHub.repository = "repo"
        conf.team.developers = ["mockitoguy:Szczepan Faber", "wwilk:Wojtek Wilk"]
        //wwilk will not be duplicated in developers/contributors
        conf.team.contributors = ["mstachniuk:Marcin Stachniuk", "wwilk:Wojtek Wilk"]

        conf.licenseInfo.license = "The MIT License"
        conf.licenseInfo.url = "https://github.com/repo/blob/master/LICENSE"

        PomCustomizer.customizePom(node, conf, "foo", "Foo library", new DefaultProjectContributorsSet())

        expect:
        printXml(node) == """<project>
  <name>foo</name>
  <packaging>jar</packaging>
  <url>https://github.com/repo</url>
  <description>Foo library</description>
  <licenses>
    <license>
      <name>The MIT License</name>
      <url>https://github.com/repo/blob/master/LICENSE</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/repo.git</url>
  </scm>
  <issueManagement>
    <url>https://github.com/repo/issues</url>
    <system>GitHub issues</system>
  </issueManagement>
  <ciManagement>
    <url>https://travis-ci.org/repo</url>
    <system>TravisCI</system>
  </ciManagement>
  <developers>
    <developer>
      <id>mockitoguy</id>
      <name>Szczepan Faber</name>
      <roles>
        <role>Core developer</role>
      </roles>
      <url>https://github.com/mockitoguy</url>
    </developer>
    <developer>
      <id>wwilk</id>
      <name>Wojtek Wilk</name>
      <roles>
        <role>Core developer</role>
      </roles>
      <url>https://github.com/wwilk</url>
    </developer>
  </developers>
  <contributors>
    <contributor>
      <name>Marcin Stachniuk</name>
      <url>https://github.com/mstachniuk</url>
    </contributor>
  </contributors>
</project>
"""
    }

    def "pom contributors from GitHub"() {
        conf.gitHub.repository = "repo"
        conf.team.developers = ["mockitoguy:Szczepan Faber", "wwilk:Wojtek Wilk"]
        conf.team.contributors = []

        conf.licenseInfo.license = "The MIT License"
        conf.licenseInfo.url = "https://github.com/repo/blob/master/LICENSE"

        //wwilk will not be duplicated in developers/contributors
        def contributorsSet = new DefaultProjectContributorsSet()
        contributorsSet.addContributor(new DefaultProjectContributor("Wojtek Wilk", "wwilk", "https://github.com/wwilk", 5))
        contributorsSet.addContributor(new DefaultProjectContributor("Marcin Stachniuk", "mstachniuk", "https://github.com/mstachniuk", 3))

        PomCustomizer.customizePom(node, conf, "foo", "Foo library", contributorsSet)

        expect:
        printXml(node) == """<project>
  <name>foo</name>
  <packaging>jar</packaging>
  <url>https://github.com/repo</url>
  <description>Foo library</description>
  <licenses>
    <license>
      <name>The MIT License</name>
      <url>https://github.com/repo/blob/master/LICENSE</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/repo.git</url>
  </scm>
  <issueManagement>
    <url>https://github.com/repo/issues</url>
    <system>GitHub issues</system>
  </issueManagement>
  <ciManagement>
    <url>https://travis-ci.org/repo</url>
    <system>TravisCI</system>
  </ciManagement>
  <developers>
    <developer>
      <id>mockitoguy</id>
      <name>Szczepan Faber</name>
      <roles>
        <role>Core developer</role>
      </roles>
      <url>https://github.com/mockitoguy</url>
    </developer>
    <developer>
      <id>wwilk</id>
      <name>Wojtek Wilk</name>
      <roles>
        <role>Core developer</role>
      </roles>
      <url>https://github.com/wwilk</url>
    </developer>
  </developers>
  <contributors>
    <contributor>
      <name>Marcin Stachniuk</name>
      <url>https://github.com/mstachniuk</url>
    </contributor>
  </contributors>
</project>
"""
    }

    def "empty team settings"() {
        conf.gitHub.repository = "repo"
        conf.team.developers = []
        conf.team.contributors = []

        conf.licenseInfo.license = "The MIT License"
        conf.licenseInfo.url = "https://github.com/repo/blob/master/LICENSE"

        PomCustomizer.customizePom(node, conf, "foo", "Foo library", new DefaultProjectContributorsSet())

        expect:
        printXml(node) == """<project>
  <name>foo</name>
  <packaging>jar</packaging>
  <url>https://github.com/repo</url>
  <description>Foo library</description>
  <licenses>
    <license>
      <name>The MIT License</name>
      <url>https://github.com/repo/blob/master/LICENSE</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/repo.git</url>
  </scm>
  <issueManagement>
    <url>https://github.com/repo/issues</url>
    <system>GitHub issues</system>
  </issueManagement>
  <ciManagement>
    <url>https://travis-ci.org/repo</url>
    <system>TravisCI</system>
  </ciManagement>
</project>
"""
    }

    def "use epl v2.0 license"() {
        conf.gitHub.repository = "repo"
        conf.team.developers = []
        conf.team.contributors = []

        conf.licenseInfo.license = "Eclipse Public License v2.0"
        conf.licenseInfo.url = "http://www.eclipse.org/legal/epl-v20.html"

        PomCustomizer.customizePom(node, conf, "foo", "Foo library", new DefaultProjectContributorsSet())

        expect:
        printXml(node) == """<project>
  <name>foo</name>
  <packaging>jar</packaging>
  <url>https://github.com/repo</url>
  <description>Foo library</description>
  <licenses>
    <license>
      <name>Eclipse Public License v2.0</name>
      <url>http://www.eclipse.org/legal/epl-v20.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/repo.git</url>
  </scm>
  <issueManagement>
    <url>https://github.com/repo/issues</url>
    <system>GitHub issues</system>
  </issueManagement>
  <ciManagement>
    <url>https://travis-ci.org/repo</url>
    <system>TravisCI</system>
  </ciManagement>
</project>
"""
    }

    private static String printXml(Node node) {
        def sw = new StringWriter()
        def printer = new XmlNodePrinter(new PrintWriter(sw))
        printer.preserveWhitespace = true
        printer.print(node)
        return sw.toString()
    }
}

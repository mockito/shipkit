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

        PomCustomizer.customizePom(node, conf, "foo", "Foo library", new DefaultProjectContributorsSet(), false)

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
        //wwilk will not be duplicated in developers/contributors
        def contributorsSet = new DefaultProjectContributorsSet()
        contributorsSet.addContributor(new DefaultProjectContributor("Wojtek Wilk", "wwilk", "https://github.com/wwilk", 5))
        contributorsSet.addContributor(new DefaultProjectContributor("Marcin Stachniuk", "mstachniuk", "https://github.com/mstachniuk", 3))

        PomCustomizer.customizePom(node, conf, "foo", "Foo library", contributorsSet, false)

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

        PomCustomizer.customizePom(node, conf, "foo", "Foo library", new DefaultProjectContributorsSet(), false)

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

    def "AAR packaging in Android library"() {
        conf.gitHub.repository = "repo"
        node.appendNode("packaging", "aar")

        PomCustomizer.customizePom(node, conf, "foo", "Foo library", new DefaultProjectContributorsSet(), true)

        expect:
        printXml(node) == """<project>
  <packaging>aar</packaging>
  <name>foo</name>
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

    private static String printXml(Node node) {
        def sw = new StringWriter()
        def printer = new XmlNodePrinter(new PrintWriter(sw))
        printer.preserveWhitespace = true
        printer.print(node)
        return sw.toString()
    }
}

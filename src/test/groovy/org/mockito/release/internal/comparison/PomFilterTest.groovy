package org.mockito.release.internal.comparison

import spock.lang.Specification

class PomFilterTest extends Specification {

    def input =
"""<?xml version="1.0" encoding="UTF-8"?>
<project>
  <version>0.2.2</version>
  <dependencies>
    <dependency>
      <groupId>org.assert</groupId>
      <artifactId>assertj-core</artifactId>
      <version>1.3.4</version>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>core</artifactId>
      <version>0.2.1</version>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>api</artifactId>
      <version>0.1.5</version>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>test</artifactId>
      <version>0.2.2</version>
      <classifier>test</classifier>
    </dependency>
  </dependencies>
</project>"""

    def filteredOutput =
"""<project>
  <dependencies>
    <dependency>
      <groupId>org.assert</groupId>
      <artifactId>assertj-core</artifactId>
      <version>1.3.4</version>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>core</artifactId>
      <version>0.2.2</version>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>api</artifactId>
      <version>0.1.5</version>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>test</artifactId>
      <version>0.2.2</version>
      <classifier>test</classifier>
    </dependency>
  </dependencies>
</project>
"""

    def underTest = new PomFilter(
            previousVersion: "0.2.1",
            currentVersion: "0.2.2",
            projectGroup: "org.mockito")

    def "removes version and sets all dependencies of the same projectGroup to the proper version"(){
        when:
        def result = underTest.filter(input)

        then:
        result == filteredOutput
    }

    def "parses correctly poms without dependencies tag"(){
        given:
        def emptyPom = "<project>" +
                "<version>0.1.2</version>" +
                "</project>"

        when:
        def result = underTest.filter(emptyPom)

        then:
        result == "<project/>\n"
    }

    def "parses correctly poms with empty dependencies tag"(){
        given:
        def emptyPom = "<project>" +
                "<version>0.1.2</version>" +
                "<dependencies></dependencies>" +
                "</project>"

        when:
        def result = underTest.filter(emptyPom)

        then:
        result == "<project>\n  <dependencies/>\n</project>\n"
    }

    def "removes contributors"(){
        given:
        def emptyPom = "<project>" +
                "<version>0.1.2</version>" +
                "<dependencies></dependencies>" +
                "<contributors>" +
                "   <contributor>" +
                "       <name>Wojtek Wilk</name>"+
                "       <url>https://github.com/wwilk</url>"+
                "   </contributor>" +
                "</contributors>" +
                "</project>"

        when:
        def result = underTest.filter(emptyPom)

        then:
        result == "<project>\n  <dependencies/>\n</project>\n"
    }

    def "removes developers"(){
        given:
        def emptyPom = "<project>" +
                "<version>0.1.2</version>" +
                "<dependencies></dependencies>" +
                "<developers>" +
                "   <developer>" +
                "      <id>wwilk</id>" +
                "      <name>Wojtek Wilk</name>" +
                "      <roles>" +
                "        <role>Core developer</role>" +
                "      </roles>" +
                "      <url>https://github.com/wwilk</url>" +
                "    </developer>" +
                "</developers>" +
                "</project>"

        when:
        def result = underTest.filter(emptyPom)

        then:
        result == "<project>\n  <dependencies/>\n</project>\n"
    }
}

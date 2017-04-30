package org.mockito.release.internal.comparison

import spock.lang.Specification

class PomSiblingDependencyRemoverTest extends Specification {

    def input =
"""<?xml version="1.0" encoding="UTF-8"?>
<project>
  <version>0.3.3</version>
  <dependencies>
    <dependency>
      <groupId>org.assert</groupId>
      <artifactId>assertj-core</artifactId>
      <version>0.2.1</version>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>core</artifactId>
      <version>0.2.1</version>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>api</artifactId>
      <version>0.2.1</version>
    </dependency>
  </dependencies>
</project>"""

    def withoutVersion =
"""<project>
  <dependencies>
    <dependency>
      <groupId>org.assert</groupId>
      <artifactId>assertj-core</artifactId>
      <version>0.2.1</version>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>core</artifactId>
      <version>0.2.1</version>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>api</artifactId>
      <version>0.2.1</version>
    </dependency>
  </dependencies>
</project>
"""

    def withoutVersionAndSiblingDependencies =
"""<project>
  <dependencies>
    <dependency>
      <groupId>org.assert</groupId>
      <artifactId>assertj-core</artifactId>
      <version>0.2.1</version>
    </dependency>
  </dependencies>
</project>
"""


    def "removes version and all dependencies on sibling projects"(){
        when:
        def result = new PomSiblingDependencyRemover().removeSiblingDependencies(input,
                [new BaseProjectProperties("org.mockito", "api"),
                 new BaseProjectProperties("org.mockito", "core")] as Set)

        then:
        result == withoutVersionAndSiblingDependencies
    }

    def "ignores independent sibling projects"(){
        when:
        def result = new PomSiblingDependencyRemover().removeSiblingDependencies(input,
                [new BaseProjectProperties("org.mockito", "independent")] as Set)

        then:
        result == withoutVersion
    }

    def "parses correctly poms without dependencies tag"(){
        given:
        def emptyPom = "<project>" +
                "<version>0.1.2</version>" +
                "</project>"

        when:
        def result = new PomSiblingDependencyRemover().removeSiblingDependencies(emptyPom,
                [new BaseProjectProperties("org.mockito", "independent")] as Set)

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
        def result = new PomSiblingDependencyRemover().removeSiblingDependencies(emptyPom,
                [new BaseProjectProperties("org.mockito", "independent")] as Set)

        then:
        result == "<project>\n  <dependencies/>\n</project>\n"
    }
}

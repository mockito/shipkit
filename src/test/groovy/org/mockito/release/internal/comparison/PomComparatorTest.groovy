package org.mockito.release.internal.comparison

import spock.lang.Specification

class PomComparatorTest extends Specification {

    def "compares poms"() {
        def pom = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-core</artifactId>
  <version>1.10.11</version>
  <dependencies>
    <dependency>
      <groupId>org.hamcrest</groupId>
      <artifactId>hamcrest-core</artifactId>
      <version>1.1</version>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-api</artifactId>
      <version>1.10.11</version>
    </dependency>
  </dependencies>
</project>"""

        def differentVersion = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-core</artifactId>
  <version>1.10.12</version>
  <dependencies>
    <dependency>
      <groupId>org.hamcrest</groupId>
      <artifactId>hamcrest-core</artifactId>
      <version>1.1</version>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-api</artifactId>
      <version>1.10.11</version>
    </dependency>
  </dependencies>
</project>"""

        def differentDependencyVersion = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-core</artifactId>
  <version>1.10.12</version>
  <dependencies>
    <dependency>
      <groupId>org.hamcrest</groupId>
      <artifactId>hamcrest-core</artifactId>
      <version>1.2</version>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-api</artifactId>
      <version>1.10.11</version>
    </dependency>
  </dependencies>
</project>"""

        def differentSiblingDependencyVersion = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-core</artifactId>
  <version>1.10.12</version>
  <dependencies>
    <dependency>
      <groupId>org.hamcrest</groupId>
      <artifactId>hamcrest-core</artifactId>
      <version>1.1</version>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-api</artifactId>
      <version>1.11.0</version>
    </dependency>
  </dependencies>
</project>"""

        expect:
        def dependentSiblingProject = [new BaseProjectProperties("org.mockito","mockito-api")] as Set
        new PomComparator(pom, pom, [] as Set).areEqual()
        new PomComparator(pom, differentVersion, [] as Set).areEqual()
        !new PomComparator(pom, differentSiblingDependencyVersion, [] as Set).areEqual()
        new PomComparator(pom, differentSiblingDependencyVersion, dependentSiblingProject).areEqual()
        !new PomComparator(pom, differentDependencyVersion, dependentSiblingProject).areEqual()
    }

    def "does not allow null content"() {
        when:
        new PomComparator(null, null, [] as Set).areEqual()
        then:
        thrown(IllegalArgumentException)
    }
}

### Celebrating contributors

See also "[How Shipkit Works](/docs/how-shipkit-works.md)" documentation index.
Please help us with docs and submit a PR with improvements!

Celebrating and appreciating contributors is important to build an engaged community around the Open Source project.
Shipkit automatically includes contributors in the release notes and in "pom.xml" file.

#### Contributors in pom.xml

When release is performed, Shipkit Gradle tasks query GitHub API to get a list of all contributors of the project.
Contributors are included in generated "pom.xml" file:

```xml
  ...
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
      <id>bric3</id>
      <name>Brice Dutheil</name>
      <roles>
        <role>Core developer</role>
      </roles>
      <url>https://github.com/bric3</url>
    </developer>
    ...
  </developers>
  <contributors>
    <contributor>
      <name>Marcin Zajączkowski</name>
      <url>https://github.com/szpak</url>
    </contributor>
    ...
```

Using GitHub we cannot reliably identify core developers (full commit rights) from all contributors.
Therefore developers of the project can be configured in shipkit file ([example shipkit.gradle](https://github.com/mockito/shipkit-example/blob/master/gradle/shipkit.gradle)):

```Gradle
shipkit {
  ...
  team.developers = ['mockitoguy:Szczepan Faber', 'wwilk:Wojtek Wilk']
}
```

#### Contributors in release notes

Release notes list contributors by name.
We show the number of commits and link to contributor's GitHub page.

**2.11.3 (2017-10-28)** - [1 commit](https://github.com/mockito/mockito/compare/v2.11.2...v2.11.3) by [Allon Murienik](https://github.com/mureinik) - published to [![Bintray](https://img.shields.io/badge/Bintray-2.11.3-green.svg)](https://bintray.com/mockito/maven/mockito-development/2.11.3)
 - InvocationsPrinter string concatination [(#1231)](https://github.com/mockito/mockito/pull/1231)

**2.11.2 (2017-10-22)** - [7 commits](https://github.com/mockito/mockito/compare/v2.11.1...v2.11.2) by [Rafael Winterhalter](http://github.com/raphw) (6), [Allon Murienik](https://github.com/mureinik) (1) - published to [![Bintray](https://img.shields.io/badge/Bintray-2.11.2-green.svg)](https://bintray.com/mockito/maven/mockito-development/2.11.2)
 - Updated Byte Buddy and ASM dependencies. Fixes #1215. [(#1218)](https://github.com/mockito/mockito/pull/1218)
 - Fixes #1183: Make override check more forgiving to accomondate Kotlin compile patterns. [(#1217)](https://github.com/mockito/mockito/pull/1217)
 - Adresses #1206: allow opting out from annotation copying within mocks. [(#1216)](https://github.com/mockito/mockito/pull/1216)
 - ClassFormatError when trying to mock certain interfaces [(#1215)](https://github.com/mockito/mockito/issues/1215)
 - Standardize JUnit imports [(#1213)](https://github.com/mockito/mockito/pull/1213)
 - Mockito should not copy annotations in all cases [(#1206)](https://github.com/mockito/mockito/issues/1206)
 - UnfinishedVerificationException with Kotlin after updating to 2.9.0 [(#1183)](https://github.com/mockito/mockito/issues/1183)

#### User guide

Tasks that generate contributors information are automatically triggered when ```performRelease``` or ```ciPerformRelease``` tasks run.
More tasks:

- During development we can preview the release notes of the next version: ```./gradlew updateReleaseNotes -Ppreview```
- We can also preview the release notes file: ```./gradlew updateReleaseNotes```
- To view the "pom.xml" file with contributors information: ```./gradlew build```, faster (no checks): ```./gradlew build -x check```, even faster (just the pom task): ```./gradlew generatePomFile```

Thank you for reading!
Questions or feedback?
Start discussion [by opening a ticket](https://github.com/mockito/shipkit/issues/new) in GitHub!

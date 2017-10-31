## How Shipkit works?

Want to find out how Shipkit works?
- see also [getting started guide](/docs/getting-started.md)
- or the entire [documentation index](/README.md#documentation)

### High level

Shipkit is a set of Gradle plugins.
Shipkit works out of the box with Java libraries or Gradle plugin projects hosted on GitHub.

Shipkit integrates with:
 - Travis CI (it's not difficult to use Shipkit with any CI)
 - Bintray (that's how we push to Maven Central)

If you already have release process you can still leverage a specific feature of Shipkit.
The best way to get started is to explain your use case to our team.
Reach out by [opening a ticket on GitHub](TODO)

### Details

How do we:
- [bump versions](/docs/features/version-bumps.md)
- [generate release notes](/docs/features/automated-release-notes.md)
- [publish binaries](/docs/features/publishing-binaries.md)
- [avoid unnecessary releases](/docs/gradle-plugins/release-needed-plugin.md)
- [automatically include contributors in pom.xml](/docs/features/celebrating-contributors.md)

Features we still work on:
- [automatically upgrade dependencies](/docs/gradle-plugins/upgrade-dependency-plugin.md)

TODO document all features mentioned in main README.md

Thank you for reading!
Questions or feedback?
Start discussion [by opening a ticket](https://github.com/mockito/shipkit/issues/new) in GitHub!

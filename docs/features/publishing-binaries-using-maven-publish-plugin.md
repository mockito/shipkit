### Publishing binaries using maven-publish plugin

See also "[How Shipkit Works](/docs/how-shipkit-works.md)" documentation index.
Please help us with docs and submit a PR with improvements!

You have the code but your users need compiled and tested binaries so that they can use your code!

#### Intro

Shipkit will automatically publish binaries to [Bintray](https://bintray.com) repository.
There is also an option to publish your artifacts to your internal Maven repository using [Maven Publish Plugin](https://docs.gradle.org/current/userguide/publishing_maven.html).

#### Nitty gritty

Applying [org.shipkit.java](https://plugins.gradle.org/plugin/org.shipkit.java) Gradle plugin will automatically apply [org.shipkit.bintray](https://plugins.gradle.org/plugin/org.shipkit.bintray) plugin what is not expected in this case.
Instead of applying `org.shipkit.java` you can apply following plugins in your `build.gradle`:

```groovy
apply plugin: "java"
apply plugin: "maven-publish"
apply plugin: "org.shipkit.travis"
apply plugin: "org.shipkit.github-pom-contributors"
apply plugin: "org.shipkit.release-notes"
apply plugin: "org.shipkit.compare-publications"
```

Now you can configure maven-publish plugin as described in [Maven Publish Plugin](https://docs.gradle.org/current/userguide/publishing_maven.html) official documentation.

```groovy
publishing {
    repositories {
        maven {
            credentials {
                username "your-user"
                password "your-password"
            }
            url "url-to-your-nexus"
        }
    }
}
```

You need also set a few task properties, e.g.:

```groovy
tasks.updateReleaseNotes.publicationRepository = "https://bintray.com/shipkit/examples/basic/"
tasks.updateReleaseNotesOnGitHub.publicationRepository = "https://bintray.com/shipkit/examples/basic/"
tasks.downloadPreviousReleaseArtifacts.previousSourcesJarUrl = "customUrl" + tasks.fetchReleaseNotes.previousVersion + "-sources.jar"
```

The first two properties are for generating a badge like:
[![Bintray](https://img.shields.io/badge/Bintray-3.0.2-green.svg)](https://bintray.com/mockito/maven/mockito-development/3.0.2)
(taken from [Mockito](https://github.com/mockito/mockito/blob/release/3.x/doc/release-notes/official.md))
in release notes file and [GitHub release page](https://github.com/mockito/mockito/releases/tag/v3.0.2).
This Badge can be a clickable link to the released version of your library in your Maven repository - you only need to set a proper link.
Shipkit will add a version at the end of the link.
If you can't expose a link to your repository you can set an empty string.

The last setting is similar, but it needs point to the previous released source jar.
E.g. if [shipkit-example](https://github.com/mockito/shipkit-example) version
[0.16.22 is build](https://travis-ci.org/mockito/shipkit-example/builds/510599683#L626) Shipkit is trying to download previous jar from:
`https://bintray.com/shipkit/examples/download_file?file_path=org/mockito/shipkit-example/impl/0.16.21/impl-0.16.21-sources.jar`.
So you can build here a proper URL to the previous `*-sources.jar`.
If you leave this setting empty, Shipkit will be not able to compare publications and will consider it as changed.
More info about comparing publications in [Avoiding unnecessary releases](/docs/gradle-plugins/release-needed-plugin.md).

And the last necessary configuration:

```groovy
allprojects {
    plugins.withId("maven-publish") {
        tasks.performRelease.dependsOn(tasks.publish)
    }
}
```

This means that `performRelease` task (from Shipkit) depends on all `publish` tasks (from maven-publish plugin).

#### Afterword

Shipkit for now doesn't support maven-publish plugin out of the box, because it wasn't a main goal at the beginning of the development.
For now described hacks are needed to make it work with maven-publish plugin.
Pull Request are welcome!

Thank you for reading!
Questions or feedback?
Start discussion [by opening a ticket](https://github.com/mockito/shipkit/issues/new) in GitHub!

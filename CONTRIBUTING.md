# Development guide

This guide describes how to contribute and develop in our team.
See [docs/how-we-work.md](docs/how-we-work.md) for info how we roll the project.
See all documentation index in [README.md](README.md#documentation).

This document contains guidelines for both groups: core developers and occasional contributors.
The purpose is to have a reference point and summary of various design discussions the team has.
It makes it easy to build high quality product if we create, update and consistently follow high level guidelines.
Doing code review is faster if we can reference clear guidelines rather than explaining the same principle again and again.

Below are **guidelines**!
They are not set in stone.
Please suggest changes or add other important guidelines.
Be pragmatic, all rules have exceptions given good reasons.

## Local development

### Building and testing

1. Clone shipkit repo, make your changes, then run ```./gradlew fastInstall```
This will install the artifacts in local maven repository for easy sharing.
Notice the version you're building in the build output.
2. Clone [shipkit-example](https://github.com/mockito/shipkit-example) repo
and ensure that 'shipkit-example/build.gradle' file uses the correct version of shipkit (declared at the top of build.gradle).
It should use the same version that was built in the previous step.
3. Basic testing for contributors:
 - Smoke test (no tasks are run): ```./gradlew performRelease -m```
 - Test most things, without actually making any code pushes/publications:
 ```./gradlew contributorTestRelease```
 - Release notes content: ```./gradlew updateReleaseNotes -Ppreview```
    To generate sizable release notes content, before running 'updateReleaseNotes' you can downgrade the 'previousVersion' in 'version.properties'.
    Release notes are generated from 'previousVersion' to current 'version' as declared in 'version.properties' file.
4. Advanced testing (occasionally, for core developers, edge cases):
 - Release notes in file: ```./gradlew updateReleaseNotes```, then inspect updated file
 - Test release needed task: ```./gradlew releaseNeeded```
 - If you are one of the core developers you can export env variables and even test git push and bintray upload.
 Run ```./gradlew testRelease``` follow the prompts and export necessary env variables.
 - Test ciReleasePrepare task but beware that it will reconfigure your working copy!
 For example, it will switch git user.
 Remember to switch it back when you finished.
 - Clone "mockito/mockito" repository and make testing with a proper project :)
5. ```testRelease``` vs. ```contributorTestRelease``` - the latter excludes tasks that need secret keys
and is useful for testing on a project where you don't have permissions to. Examples:
If you are contributor out of the core team, use ```contributorTestRelease``` with shipkit-example.
If you are core team member, use ```testRelease``` with shipkit-example.
If you are contributor, you can use ```testRelease``` with your own project that you have permissions to.

### Troubleshooting releases

Sometimes we cannot release new version Shipkit because there is a bug in Shipkit.
Since we cannot release, we cannot release a bugfix.
To break the stalemate, we need to release from local, for example:

```
./gradlew testRelease
./gradlew performRelease
```

## Dependencies on external libraries

Reuse is awesome but it comes with a cost.
Version conflicts causing weird errors can be really painful for the end users.
Anything that can be painful for end users lowers the chance of adoption of the tool.

### Minimizing external dependencies

By default we want to avoid external dependencies.
Our users will use our plugins with many other plugins we don't know about.
Those other plugins can bring other dependencies to the jar party.
The more dependencies, the more complicated the dependency graph, the more chance for version conflicts.
The more version conflicts, the higher chance one of them will actually break the build.

## Gradle plugins guidelines

Gradle plugins have certain conventions.
Adhering to the conventions make our plugins easier to use.
Straying away from conventions will increase complexity and will make the behavior surprising for users.
Let's try to stick to conventions and developer great set of Gradle plugins!

### Build directory

By convention, Gradle tasks write outputs (binaries, jars, compiled code, any temporary build results etc.) to “build” directory.
To be more precise, the tasks write to the directory specified by “project.buildDir” (by default it is “build” directory).
Plugins and tasks we develop need to follow that convention and write any outputs to “project.buildDir”.

### Cleaning build directory

By convention, when user runs “gradle clean” all stuff that is produced by the build is cleaned.
Every plugin that adds tasks that write anything to "project.buildDir" should also apply Gradle’s “base” plugin.
“base” plugin automatically adds “clean” task that removes "project.buildDir".

It is important that our plugins do not add “clean” task explicitly because it will make our plugins hard to use. It won’t be possible for the user to apply our plugin and any of the Gradle’s vanilla plugins like “java” plugin.
Gradle will throw an exception during configuration time: "task clean cannot be added because such task already exists”.

## API

### Public and internal api

1. Top level package is "org.shipkit".
2. Public API are classes and interfaces that we guarantee compatibility after 1.0 release.
    All public classes live under "org.shipkit.*", nested accordingly for clarity.
3. Internal API can change at any time, we don't guarantee compatibility.
    Internal types live under "org.shipkit.internal.*", nested accordingly for clarity.
    Users are welcome to use internals for experimentation and working around gnarly use cases or bugs.
    Please let us know if you need an internal type and why!
    This way we can create public API for you to use!
    Finally, keep in mind that internal types can change without notice.
4. Gradle plugins are public and we guarantee compatibility of behavior after 1.0 release.
    For example, we will not remove or rename a task that is added by a plugin.
    Plugin implementation classes are considered "internal" because they don't have public facing API.
    The plugin should be referred via the "plugin id", examples:

```Groovy
apply plugin: "org.shipkit.java"

plugins.withId("org.shipkit.java") {
   ...
}
```

## Testing

### Unit testing

Let's write tons of great tests :)

tbd

### Integration testing

tbd


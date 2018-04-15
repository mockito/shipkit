# Problem

Local snapshot versions are useful when performing local end-to-end testing:
we build snapshot version of a library, then consume this library in a different project.
Using “-SNAPSHOT” version in this scenario is useful because it prevents version confusion,
e.g. locally built version “1.2.10” of the library is different than externally available version “1.2.10”.

Tickets:
 - original user feedback: [#358](https://github.com/mockito/shipkit/issues/358)
 - current work ticket: [#692](https://github.com/mockito/shipkit/issues/692)

## Creating snapshots

New task “snapshot” builds a snapshot version and installs it to a local repository.
The task optimizes for speed so that the user can engage e2e testing rapidly, without the need to fix unit tests or checkstyle.
Hence, we avoid running tests or building javadoc when building and installing the snapshot.

Examples:

 - when "./gradlew build" produces "1.5.10" then "./gradlew snapshot" produces "1.5.10-SNAPSHOT"
 - when "version.properties" contains "1.5.10" then "./gradlew snapshot" produces "1.5.10-SNAPSHOT"
 - when "version.properties" has "1.5.10-SNAPSHOT" (already a snapshot) then "./gradlew snapshot" produces the same version, e.g. "1.5.10-SNAPSHOT"

Additional requirements:

 - "snapshot" avoid tests/checks and generation of Javadoc/Groovydoc by default.
 - by default "snapshot" installs Maven artifacts to local maven repo.
 - in future, an option to build local ivy snapshot via separate plugin

## Producing snapshots

- New LocalSnapshotPlugin
    - id: "org.shipkit.local-snapshot"
    - package: org.shipkit.internal.gradle.version
    - adds new task "snapshot" task (DefaultTask), no behavior, it is a placeholder/anchor task

- Existing VersioningPlugin
    - applies LocalSnapshotPlugin for "snapshot" task
    - if "snapshot" task is requested by the user, append "-SNAPSHOT" to version
        - if "snapshot" or ":foo:bar:snapshot" in "gradle.startParameter.taskNames" add “-SNAPSHOT"
    - in addition, if "snapshot" project property is present, add "-SNAPSHOT" to version
    - don't append "-SNAPSHOT" if version already has it (for example, in "version.properties" file)

- Existing JavaPublishPlugin
    - applies LocalSnapshotPlugin for "snapshot" task
    - snapshot.dependsOn publishToMavenLocal
    - disable javadoc and groovydoc if snapshot in DAG - speeeed!

- Existing GradlePortalPublishPlugin
    - applies new LocalMavenSnapshotPlugin ("org.shipkit.local-maven-snapshot", same package as LocalSnapshotPlugin)
        - applies LocalSnapshotPlugin for "snapshot" task
        - applies Gradle's built-in "maven" plugin for "install" task
        - snapshot.dependsOn install
        - disable javadoc and groovydoc if snapshot in DAG - speeeed!

### Future ivy support

In order to add ivy local repo support, we can roll out a new plugin.
Users wishing to install to local ivy repo can apply "org.shipkit.local-ivy-snapshot"
to every module that produces artifacts.

Low priority but likely needed for onboarding [linkedin/play-parseq](https://github.com/mockito/shipkit/issues/673) project.

## Consuming snapshots

Running "snapshot" in producer project assembles snapshot artifacts
and also writes a snapshot info file ("~/.gradle/shipkit/snapshot.properties")

Consumer project can run "useSnapshot" task to quickly start using
the snapshot that was most recently built on this machine.
"useSnapshot" updates dependencies in *.gradle files to use the snapshot version
found in snapshot info file.

## Writing snapshot info

Snapshot info in "~/.gradle/shipkit/snapshot.properties" file has "group=version" format:

```
org.shipkit=1.2.3-SNAPSHOT
```

 - LocalSnapshotPlugin applies RootLocalSnapshotPlugin to the root project
 - RootLocalSnapshotPlugin adds ":writeSnapshotInfo" task of type WriteSnapshotInfoTask
    - WriteSnapshotInfoTask has property: "snapshotInfoFile" defaulted to
    "${project.gradle.gradleUserHomeDir}/shipkit/snapshot.properties"
    - when task completes, it informs that snapshots were created and can be used with 'useSnapshot'
    - the message contains link to more info: http://local-snapshot.shipkit.org
 - "snapshot" task added by LocalSnapshotPlugin is finalized by ":writeSnapshotInfo"
 - Caveat: because we use "finalized by", the "writeSnapshotInfo" will write the info
    regardless if the build is successful or failed. This should be fine becuase if the build fails
    the user does not have expectation that the snapshot works and would not attempt to run "useSnapshot".
    At times, it can be useful that we write snapshot info on failed build
    becuase expert users can leverage it.
    Good example is running "./gradlew snapshot check --continue"
    with the intention to build snapshot and run checks after that.
    Even if checkstyle check fails, the expert users may want to test with local snapshot.

## Reading snapshot info

 - RootLocalSnapshotPlugin adds ":useSnapshot" task of type UseSnapshotTask, with properties:
    - File snapshotInfoFile (same default as WriteSnapshotInfoTask)
    - List<File> buildFiles - build files where we will attempt to update the dependency version.
    Build files are defaulted to all build files of all projects in the build
 - Updating dependencies is simple and only supports String notation. Example:
 If snapshot info has "org.shipkit=1.2.3-SNAPSHOT" then we will update all
 "org.shipkit:(module):(version)" with "org.shipkit:(module):1.2.3-SNAPSHOT"
 - We write a one-line summary with how many files were updated
 - We fail with meaningful message when:
    - there is no snapshot info file or it cannot be read
    - there is no dependency to update in any of the build files

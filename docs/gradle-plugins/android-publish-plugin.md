### Android libraries support

Android Gradle Plugin `3.6.0-beta05` or newer is required.

Configuration specific to Android library projects (using `com.android.library` plugins):

1. Apply `org.shipkit.android-publish` plugin to each Gradle project (submodule) you want to publish
(usually they are not the root projects).
1. Specify `artifactId` in `androidPublish` blocks.

Example:

```Gradle
apply plugin: 'org.shipkit.bintray'
apply plugin: 'org.shipkit.android-publish'
apply plugin: 'com.android.library'

androidPublish {
    artifactId = 'shipkit-android'
}

```

Other POM properties which can be set using Gradle API:
* group id - [Project#group](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:group)
* name - [Project#archivesBaseName](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:archivesBaseName)
* description - [Project#description](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:description)

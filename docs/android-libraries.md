### Android libraries support

Configuration specific to Android library projects (using `com.android.library` plugins):

1. Apply `org.shipkit.android-publish` plugin to each Gradle project (submodule) you want to publish (usually it is not a root project).
2. Specify `artifactId` in `androidPublish` blocks. 

Example:

```Gradle
apply plugin: 'org.shipkit.bintray'
apply plugin: 'org.shipkit.android-publish'
apply plugin: 'com.android.library'

androidPublish {
    artifactId = 'shipkit-android'
}

```

Other POM properties can be set using Gradle API:
* group id - [Project#group](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:group) 
* name - [Project#archivesBaseName](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:archivesBaseName) 
* description - [Project#description](https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:description) 
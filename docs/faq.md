## Frequently asked questions

See also the entire [documentation index](/README.md#documentation)

### How do I get started?

Check out following documents:

 - Getting started: [docs/getting-started.md](/docs/getting-started.md)
 - How Shipkit works: [docs/how-shipkit-works.md](/docs/how-shipkit-works.md)

### How do I prevent publication of specific submodule?

By default Shipkit plugins such as "org.shipkit.java" publish all submodules (jars) of the project.
Using Gradle vocabulary: all archives produced by all subprojects in your build are published.
Say that one of your subprojects is a "test" or "sample" project that you don't want to publish.
To prevent publication, disable ```bintrayUpload``` task:

```groovy
//build.gradle of a subproject that you don't want to publish
apply plugin: 'java'
...
bintrayUpload.enabled = false
```

### How to build my library against different JDK versions?

Sometimes projects are build against different Java versions, but you can't release the same 
artifact version twice. To avoid failed builds on Travis, you can configure it like that:

```yaml
matrix:
  include:
  - jdk: oraclejdk8
  - jdk: oraclejdk9
    env: SKIP_RELEASE=true
  - jdk: openjdk10
    env: SKIP_RELEASE=true
  - jdk: openjdk11
    env: SKIP_RELEASE=true
```   

Now only artifacts produced by JDK8 build will be published.

### How to publish artefacts to the internal repository?

Please read [Publishing binaries using maven-publish plugin](/docs/features/publishing-binaries-using-maven-publish-plugin.md).

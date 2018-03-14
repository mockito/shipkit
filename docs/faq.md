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

```java
//build.gradle of a subproject that you don't want to publish
apply plugin: 'java'
...
bintrayUpload.enabled = false
```

See [docs/getting-started.md](/docs/getting-started.md) document.



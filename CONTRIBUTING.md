# Development guide

This file contains guidelines for both groups: core developers
 and occasional contributors.

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

1. Top level package is "org.mockito.release".
2. Public API are classes and interfaces that we guarantee compatibility after 1.0 release.
    All public classes live under "org.mockito.release.*", nested accordingly for clarity.
3. Internal API can change at any time, we don't guarantee compatibility.
    Internal types live under "org.mockito.release.internal.*", nested accordingly for clarity.
    Users are welcome to use internals for experimentation and working around gnarly use cases or bugs.
    Please let us know if you need an internal type and why!
    This way we can create public API for you to use!
    Finally, keep in mind that internal types can change without notice.
4. Gradle plugins are public and we guarantee compatibility of behavior after 1.0 release.
    For example, we will not remove or rename a task that is added by a plugin.
    Plugin implementation classes are considered "internal" because they don't have public facing API.
    The plugin should be referred via the "plugin id", examples:

```Groovy
apply plugin: "org.mockito.mockito-release-tools.continuous-delivery"

plugins.withId("org.mockito.mockito-release-tools.continuous-delivery") {
   ...
}
```

## Testing

### Unit testing

Let's write tons of great tests :)

tbd

### Integration testing

tbd


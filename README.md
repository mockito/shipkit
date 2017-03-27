## Imagine

Imagine the world where you call pull in a new version of some Open Source library and not worry if it breaks compatibility. Imagine that you can submit a pull request to some project, have it reviewed timely, and have the new version with your fix available to you in minutes after your PR is merged. Imagine that for any dependency you consider upgrading, you can view its neatly and consistently maintained release notes. Imagine that you can set up practical Continuous Delivery automation in your project in minutes, by using a well behaving and documented Gradle plugin. Imagine that you can focus on code and features while the release management, versioning, publishing, release notes generation is taken care for you automagically.

This is the goal of "mockito release tools project". The project started in November 2016 and is currently in progress.

## True North Star

The goal Mockito Release Tools project is to provide easy-to-setup Continuous Delivery tooling. We would like other teams to take advantage of rapid development, frictionless releases and semantic versioning just like [we do it in Mockito](https://github.com/mockito/mockito/wiki/Continuous-Delivery-Overview). We plan to make our release tools generic and neatly documented. It will be a set of libraries and Gradle plugins that greatly simplify enabling Continuous Delivery for Java libraries.

We already use this project to drive [Continuous Delivery](https://github.com/mockito/mockito/wiki/Continuous-Delivery-Overview) of Mockito Java library
and automatically publish new versions to
[2M Mockito users](https://github.com/mockito/mockito/wiki/Mockito-Popularity-and-User-Base)!

## We need help!

If the vision of the project connects with you help us!!! Get in touch on the [mailing list](https://groups.google.com/forum/#!forum/mockito-release-tools).

- Implementing features - see the issue marked with "[please contribute](https://github.com/mockito/mockito-release-tools/issues?q=is%3Aissue+is%3Aopen+label%3A%22please+contribute%21%22)" label.
- Using the release tools in your project and giving us feedback.
- Spreading the word of what we're doing, letting us know about other project with similar goals. You can GitHub issue tracker for reaching out.

### Testing

#### Release notes

To develop improvements in release notes automation and test with Mockito project follow the steps:

1. Clone mockito-release-tools repo, make your changes, run './gradlew install' task.
 This will install the artifacts in local maven repository for easy sharing.
 Notice the version you're building in the build output.
2. Clone mockito-release-tools-example repo and ensure that 'release-tools-example/build.gradle' file uses the correct version of mockito-release-tools (declared at the top of build.gradle).
 It should use the same version that was built in the previous step.
3. Tests you can do in mockito-release-tools-example:
  - run './gradlew testRelease'
  - edit the 'doc/release-notes.md' file.
   Delete one or many versions from the top of the file so that release notes generation will regenerate them.
   Run './gradlew previewReleaseNotes' and inspect the console output.

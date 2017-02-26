### Mockito Release Tools

The project aspires to help your team establish high quality release automation for libraries. Features:
 - automatically generated release notes
 - frequent, transparent releases

We already use this project to drive [Continuous Delivery](https://github.com/mockito/mockito/wiki/Continuous-Delivery-Overview) of Mockito Java library
and automatically publish new versions to
[2M Mockito users](https://github.com/mockito/mockito/wiki/Mockito-Popularity-and-User-Base)!

### Please contribute!

Do you want to help?
Grab one of the fully scoped features, [ready to be developed](https://github.com/mockito/mockito-release-tools/issues?q=is%3Aissue+is%3Aopen+label%3A%22please+contribute%21%22).
Submit a pull request!

### Motivation

The immediate goal of this project is to expose the continuous delivery automation logic as general purpose set of libraries and Gradle plugins.
This way, it can be used by other software projects in the Open Source or in the enterprise.
We hope that more libraries and Open Source projects can use this automation to establish healthy, continual releases with consistent release notes.

This project started in November 2016 and is currently in progress.

### Testing

#### Release notes

To develop improvements in release notes automation and test with Mockito project follow the steps:

1. Clone mockito-release-tools repo, make your changes, run './gradlew install' task.
 This will install the artifacts in local maven repository for easy sharing.
2. Clone mockito repo and ensure that mockito 'gradle.properties' file has correct version of mockito-release-tools.
 It should declare the same version of release tools that was built in the previous step.
3. In mockito clone, edit the 'doc/release-notes/official.md' file.
 Delete one or many versions from the top of the file so that release notes generation will regenerate them.
 For example, if 'offcial.md' has version '2.7.6' at the top, remove it so that there is '2.7.5' version at the top.
4. Run './gradlew previewReleaseNotes' and inspect the console output.
5. You can delete more versions (step 3) and preview release notes (step 4) to view a larger set of improvements included in the release notes.

Soon we will make e2e testing of the release notes automation easier!
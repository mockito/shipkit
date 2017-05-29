# Shipkit

## Imagine

Imagine the world where you call pull in a new version of some Open Source library and not worry if it breaks compatibility. Imagine that you can submit a pull request to some project, have it reviewed timely, and have the new version with your fix available to you in minutes after your PR is merged. Imagine that for any dependency you consider upgrading, you can view its neatly and consistently maintained release notes. Imagine that you can set up practical Continuous Delivery automation in your project in minutes, by using a well behaving and documented Gradle plugin. Imagine that you can focus on code and features while the release management, versioning, publishing, release notes generation is taken care for you automagically.

This is the goal of "Shipkit project". The project started in November 2016 and is currently in progress.

## True North Star

The goal Mockito Release Tools project is to provide easy-to-setup Continuous Delivery tooling. We would like other teams to take advantage of rapid development, frictionless releases and semantic versioning just like [we do it in Mockito](https://github.com/mockito/mockito/wiki/Continuous-Delivery-Overview). We plan to make Shipkit generic and neatly documented. It will be a set of libraries and Gradle plugins that greatly simplify enabling Continuous Delivery for Java libraries.

We already use this project to drive [Continuous Delivery](https://github.com/mockito/mockito/wiki/Continuous-Delivery-Overview) of Mockito Java library
and automatically publish new versions to
[2M Mockito users](https://github.com/mockito/mockito/wiki/Mockito-Popularity-and-User-Base)!

## We need help!

If the vision of the project connects with you help us!!! Get in touch on the [mailing list](https://groups.google.com/forum/#!forum/mockito-release-tools).

- Implementing features - see the issue marked with "[please contribute](https://github.com/mockito/mockito-release-tools/issues?q=is%3Aissue+is%3Aopen+label%3A%22please+contribute%21%22)" label.
- Using the Shipkit in your project and giving us feedback.
- Spreading the word of what we're doing, letting us know about other project with similar goals. You can GitHub issue tracker for reaching out.

## History

- 2014, August - we set up automated [continuous delivery for Mockito](http://blog.mockito.org/2014/08/ready-for-continuous-deployment.html) project, every merged pull requested produced release notes and new version in Maven Central.
We [still do it today](https://github.com/mockito/mockito/wiki/Continuous-Delivery-Overview) in Mockito!
- 2016, November - we pushed out the release automation toolkit to a separate project in GitHub, called "mockito-release-tools".
We really needed to do that!
As much as useful the toolkit was to drive Mockito releases, we needed to make it reusable, well documented, and packaged as a separate binary.
- 2017, February - [Marcin Stachniuk](https://github.com/mstachniuk) joins the team, implements automatic fetching of contributors from GitHub and starts working on automatic [e2e testing](https://github.com/mockito/mockito-release-tools/issues/85) of library clients.
- 2017, March - [Wojtek Wilk](https://github.com/wwilk) joins the team, implements release avoidance when binaries aren't changed in comparison to previous release, starts driving [API compatibility](https://github.com/mockito/mockito-release-tools/issues/105) validation and Shipkit dogfooding.
- 2017, April - we found a neat name for our project: "Shipit", a toolkit for shipping it! We registered the domain: http://shipkit.org
- 2017, May - there are 5 contributors in total, we expanded the vision of the project, and currently working on killer features like: automated e2e testing with library consumers + automated pushes of version upgrades [#85](https://github.com/mockito/mockito-release-tools/issues/85), [A/B testing](https://github.com/mockito/mockito-release-tools/issues/113) of build results when upgrading dependencies.

## Plans

- 2017, June - we plan to release [1.0 of Shipkit](https://github.com/mockito/mockito-release-tools/issues/116) library during [Gradle Summit Conference](https://summit.gradle.com) 22-23th of June 2017, Palo Alto, CA. Core features offered by 1.0:
  - version management
  - automatic publication of binaries to well known public repositories
  - release notes generation based on Git log and the issue tracker links
  - avoiding releases when binaries are the same
  - pulling information about contributors and including it in release notes and pom.xml files
  - and all that in a sweet little package, fully integrated, neatly automated, easy to roll out
- 2017, July - we will be present at [Confitura '17 conference](https://2017.confitura.pl) in Warsaw, PL, on 1st of July. We submitted a paper, if it gets accepted, Shipkit will be revealed in all its might!

## Future

The roadmap is comming soon!

## Development guide

See CONTRIBUTING.md

## Project Execution

This section describes how we roll the project.

### Getting contributions

Contributions are super important to build lively community and excite other engineers around the project vision. In "mockito-release-tools" we use ["please contribute!"](https://github.com/mockito/mockito-release-tools/issues?q=is%3Aissue+is%3Aopen+label%3A%22please+contribute%21%22) label on tickets to attract contributors. Before the ticket can be labeled with "please contribute!" it should:
 - sell the feature. If the value of the ticket is not clear, why would a contributor bother to invest his free time?
 - describe high level design. Otherwise it's hard to come up with the implementation. More over, lack of guiding high level design leads to more rework and ping-pong pull requests, with multiple pr->feedback->fixes cycles.
 - give a starting point, point to classes in question, suggest implementation, or even point to the branch that has some scaffolding prototype code. Example ["starting point" PR](https://github.com/mockito/mockito-release-tools/pull/100).
 - be like [#101](https://github.com/mockito/mockito-release-tools/issues/101), [#84](https://github.com/mockito/mockito-release-tools/issues/84), or [epic #85](https://github.com/mockito/mockito-release-tools/issues/85)

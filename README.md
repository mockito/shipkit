We are in the process of renaming the repository to "Shipkit". Thank you for patience!

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

### How to give great code review feedback?

We can teach anyone to code but we cannot teach anyone to love coding.
Great code reviews can.
Great code review feedback streamlines team's productivity, amplifies learning and makes working together fun and engaging.

#### Great code review comments, non-technical

- try to give plenty of detailed, positive feedback.
It needs details because otherwise it does not have the positive effect we want to create.
We want fun, engaging environment where it’s a pleasure engineering!
If you see something nice, say it: “I like this change because it reduces complexity - we no longer need to maintain ...”, “-100 lines of code out our project - less maintenance, faster delivery!”.
- try to show gratitude and cherish every PR.
If there are no PRs from others, you would have to implement everything yourself.
One man wolf pack can hardly delivery an important, big project.
- try to avoid nitpicking, especially related to code formatting.
Tools should validate code format, not humans!
Instead of nitpicking on code format, setup automation to validate code style automatically.
Nitpicking diverts attention from important code comments, creates discussions around low priority code changes, slows down team’s momentum.
- try to thoroughly explain the “why”.
What’s obvious to you, is never obvious to other people.
Examples: “extracting out this class will save 50 lines of code”, “this refactoring will make the code easier to test because …”.
- try to clearly indicate if you want an action to be taken.
A comment like “It would be nice to change …” is not very clear.
How about “long term we might do X, at the moment no action needed”, “let’s add more unit tests”.
- try to clearly indicate importance: “this will break X”, “I think it is really important to ...”
- try to accept how others think.
There is no single, golden way of implementing any feature.
Request changes but respect if the submitter wants to do things his way.
Example: make recommendation how to change the code but still approve the PR and let the submitter decide whether to take the recommendation or not.
This typically applies only to core team members because outside contributors cannot merge.

#### Code review technical hints

- try to keep the momentum, allow to merge PRs even though some rough edges are not smooth yet, so long we don’t break compatibility.
Splitting bigger changes across multiple PRs is sometimes very useful and enables to keep nice pace of development.
Otherwise we’re bogged down reviewing the same PR over and over again.
Example: “this PR is getting big. How about we push ... but leave … for the next PR?”.
- pushing directly to the branch the contributor submitted the PR from is very powerful.
Instead of leaving lots of small suggestions that are typically hard to manage in any code review tool, you can make changes directly on the contributor’s branch and push.
This technique is very useful when pushing TODOs directly to the submitter’s branch.
TODOs are preserved in code / Git history and are very easy to manage, hard to forget.
Example, a TODO for team member with ‘SF’ initial: “TODO SF - can you try to …”.
- commit-by-commit review is very powerful. When working on a big change, having many commits usually helps.
Some commits are automated refactorings like renames and they don’t need a lot of scrutiny.
Some commits change the core of an algorithm and need to be reviewed very thoroughly.
The PR submitter can even indicate in PR description what commits require scrutiny.
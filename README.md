# Shipkit

[![Build Status](https://travis-ci.org/mockito/shipkit.svg?branch=master)](https://travis-ci.org/mockito/shipkit)

## Documentation

- Project information: [README.md](README.md)
    - Help us! How to contribute: [CONTRIBUTING.md](CONTRIBUTING.md)
    - Work with us! How we work: [docs/how-we-work.md](docs/how-we-work.md)
    - Shipkit release notes: [docs/release-notes.md](docs/release-notes.md)
- User guides
    - Getting started: [docs/getting-started.md](docs/getting-started.md)
    - How Shipkit works: [docs/how-shipkit-works.md](docs/how-shipkit-works.md)

## Imagine

You will be more productive if your releases are fully automated and happen on every change.
You will build great product if you can focus on code & features, but not on the release overhead.
Shipkit will make it happen.
Shipkit is a toolkit for shipping it.

Every team should be empowered to develop with rapid velocity, frictionless releases and semantic versioning just like [we do it in Mockito](https://github.com/mockito/mockito/wiki/Continuous-Delivery-Overview).
Shipkit enables Mockito to automatically publish new versions to
[2M Mockito users](https://github.com/mockito/mockito/wiki/Mockito-Popularity-and-User-Base)!

<details>
  <summary>Fully automated releases are only the first part of the journey...</summary>

  Imagine the world where you call pull in a new version of some Open Source library and not worry if it breaks compatibility.
  Imagine that you can submit a pull request to some project, have it reviewed timely, and have the new version with your fix available to you in minutes after your PR is merged.
  Imagine that for any dependency you consider upgrading, you can view its neatly and consistently maintained release notes.
  Imagine that you can set up practical Continuous Delivery automation in your project in minutes, by using a well behaving and documented Gradle plugin.
  Imagine that you can focus on code and features while the release management, versioning, publishing, release notes generation is taken care for you automagically.

  This is the goal of "Shipkit" project.
</details>

### Customers

Using Shipkit? Submit a pull request and add your project to the list!

- Mockito: https://github.com/mockito/mockito
- Powermock: https://github.com/powermock/powermock
- Shipkit: https://github.com/mockito/shipkit
- Shipkit example: https://github.com/mockito/shipkit-example

### Help us!

If the vision of the project connects with you help us!!!

- Open a [new GitHub ticket](https://github.com/mockito/shipkit/issues/new) to start the conversation. We love feedback, brainstorming and discussions.
- Drop a comment to one of the existing "[please contribute](https://github.com/mockito/shipkit/issues?q=is%3Aissue+is%3Aopen+label%3A%22please+contribute%21%22)" tickets, and tell us that you are interesting in implementing it.
- Try out Shipkit in your project and give feedback
- If you like emails, join the [mailing list](https://groups.google.com/forum/#!forum/shipkit), but be warned that we rarely use emails, and prefer GitHub tickets.
- Spread the word about Shipkit, let us know about other projects with similar goals.

## Features

Currently, Shipkit offers Gradle plugins for automating releases of Java libraries and Gradle plugins.
Basic use case is simple:
You have code that wants to be delivered to your customers, Shipkit has tools to help you out:

- automatic version bumps in "version.properties" file - don’t waste time managing your version manually
- automatically generated release notes in markdown - offer your customer clean information what changed and why
- automatic including contributors in pom.xml - appreciate the community
- release notes that highlight each individual contributor by name - build engaged community
- avoiding publishing binaries if nothing changed - respect your customers’ time
- automatic shipping to Bintray and Maven Central - use every opportunity to give your product to the hands of customers
- keeping secure tokens safe - masks sensitive values from logging and error messages from underlying tools like git
- and all that in a sweet little package, fully integrated, neatly automated, easy to roll out

For details see "[How Shipkit Works?](/docs/how-shipkit-works.md)"

## Quick start

Add Gradle plugin:
```groovy
plugins {
  // TODO: Use latest version from https://plugins.gradle.org/plugin/org.shipkit.java
  id "org.shipkit.java" version "0.9.79"
}
```

Initialize:
```
./gradlew initShipkit
```

Perform release:
```
./gradlew performRelease
```

For more and detailed information see [docs/getting-started.md](docs/getting-started.md)

### History

- 2014, August - we set up automated [continuous delivery for Mockito](http://blog.mockito.org/2014/08/ready-for-continuous-deployment.html) project, every merged pull requested produced release notes and new version in Maven Central.
We [still do it today](https://github.com/mockito/mockito/wiki/Continuous-Delivery-Overview) in Mockito!
- 2016, November - we pushed out the release automation toolkit to a separate project in GitHub, called "mockito-release-tools".
We really needed to do that!
As much as useful the toolkit was to drive Mockito releases, we needed to make it reusable, well documented, and packaged as a separate binary.
- 2017, February - [Marcin Stachniuk](https://github.com/mstachniuk) joins the team, implements automatic fetching of contributors from GitHub and starts working on automatic [e2e testing](https://github.com/mockito/shipkit/issues/85) of library clients.
- 2017, March - [Wojtek Wilk](https://github.com/wwilk) joins the team, implements release avoidance when binaries aren't changed in comparison to previous release, starts driving [API compatibility](https://github.com/mockito/shipkit/issues/105) validation and Shipkit dogfooding.
- 2017, April - we found a neat name for our project: "Shipkit", a toolkit for shipping it! We registered the domain: http://shipkit.org
- 2017, May - there are 5 contributors in total, we expanded the vision of the project, and currently working on killer features like: automated e2e testing with library consumers + automated pushes of version upgrades [#85](https://github.com/mockito/shipkit/issues/85), [A/B testing](https://github.com/mockito/shipkit/issues/113) of build results when upgrading dependencies.
- 2017, June - "Ship every change to production!" presentation at Software Architecture conference in Santa Clara, CA ([abstract](https://docs.google.com/document/d/1K96_v5SZEwnmUp2ZLej8en_sd8EaXa4y1lw48rtN8Bs)).
- 2017, June - Szczepan includes information about Shipkit in his presentation at Gradle Summit conference in Palo Alto, CA. ([abstract](https://summit.gradle.com/conference/palo_alto/2017/06/session?id=39273), [slides](http://prezi.com/ok5z9lflwejm/?utm_campaign=share&utm_medium=copy), [video](https://www.youtube.com/watch?v=7N2sg2X_HrA&feature=youtu.be&t=43m12s))
- 2017, July - Shipkit presented at Confitura '17 conference in Warsaw ([abstract](https://2017.confitura.pl/presentations#531c0ef5-5bb9-4c6c-9822-d5757918e8b4), [slides](https://docs.google.com/presentation/d/1ocBAg4Jq07TP7rpROMJGR5I-qeNza9E1pwZ6elko4w8/edit?usp=sharing), [video](https://youtu.be/EQNZWCkwnAI?t=5h52m8s))
- 2017, July/August - Shipkit presentations at JUG meetups in Poland: [all abstracts](https://docs.google.com/document/d/15V4EReNQcDNUqPKhVa6N8Vyi87sKAG65RWyRCoBHLcU/edit#), Krakow ([slides](https://docs.google.com/presentation/d/1MLxVd_4YtPS00hOK8zBVSaoHs-EsdMvTJse8lE45W2o/edit?usp=sharing)), Wroclaw ([slides](https://docs.google.com/presentation/d/1OH2L5Okplqa_sfteSycBVCFdLPgpUmSTR9IYNVCm4Zo/edit?usp=sharing)), and Bielsko-Biala ([slides](https://docs.google.com/presentation/d/1LuDC78iQ-404INYF62bWNUMllqyZEFKfQBu_nRliFxo/edit?usp=sharing)).

Want to include your event? Submit a pull request!

### Plans

- 2017, October - Szczepan has 3 talks at JavaOne (San Francisco).
Mockito productivity talk will mention Shipkit.
Other talks: Engineering at scale with [Gradle and Play Framework at LinkedIn](https://www.linkedin.com/pulse/javaone-talk-play-framework-gradle-productivity-linkedin-faber/), Commit-to-production pipeline at LinkedIn (TODO link).
- 2017, October - we plan to release [1.0-milestone-1 of Shipkit](https://github.com/mockito/shipkit/issues/116).
- 2017, November - Szczepan's [continous delivery talk at QCon](https://qconsf.com/sf2017/presentation/lessons-linkedin-and-mockito) (San Franciso), will include Mockito use of Shipkit.
- 2017, Q4 - 1.0 release.
- 2018, helping community adopt Shipkit while adding writing great features on the way!

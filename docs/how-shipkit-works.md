## How Shipkit works?

Want to find out how Shipkit works?
- see also [getting started guide](/docs/getting-started.md)
- or the entire [documentation index](/README.md#documentation)

### Version bumps

Yesterday version is already old.
Frequent, automated releases need some way to manage incrementing versions.
Welcome to continuous delivery!

#### Intro

Shipkit needs to be able to automatically use the right version when building and publishing.
This way, you don't need to deal with changing/updating the version for every release.
Every release will produce n+1 version.

There are multiple options how to deal with version bumps.
Some projects automatically infer the version of the next release by looking at the nearest tag in git.
This approach is interesting but it gets complicated when you want to release specific version (like 1.0).
Shipkit uses a simple ```version.properties``` file that contains the version number.

#### Nitty gritty

Every release, the version file is automatically updated with a new version number.
The next version after "1.0.0" is "1.0.1", then "1.0.2", etc.
If you need to release a specific version, like "1.1.0" or "2.0.0" just update ```version.properties``` file manually and include that in your pull request.

We received feedback that using version file clutters git history with version bump commits.
Since we need to commit changes to release notes / changelog file to the repository, version bumps actually don't introduce any extra commits.
There is already a commit for every release.
Also, we find it useful to see the progression of releases when reviewing git log.
When debugging, learning the codebase, we usually review git history for specific source file.
Therefore, high volume of changes to ```version.properties``` is not really troublesome.

Shipkit uses ```version.properties``` file also for other version related information, like the version of previous release.
It is used for automated generation of release notes.

#### User guide

Shipkit will automatically update the version file on every release so you don't really need to do anything.
Occasionally, you might need to release a more notable version like new major version.
In this case, just update the file manually and commit that change.
Shipkit will build and release the version as specified in the file.

What do you think? Start discussion by opening a ticket in GitHub!

### Release notes

Your users need to know what they are getting when upgrading versions.
You want to build great software? Have a great changelog.
You want to build community? Have every contributor listed by name in the release notes.

#### Intro

Shipkit will automatically generate high quality, human readable, well formatted release notes markdown file.
You only need to include #id of the GitHub ticket number in commit messages.
That happens automatically, if you follow the GitHub pull request model.

Shipkit will motivate you to work in your project with higher craftsmanship:
 - the better descriptions of your GitHub tickets / pull requests -> the better the release notes
 - the more disciplined the team is around using pull requests on daily basis -> the more content in release notes

#### Nitty gritty

During the release, Shipkit loads git log and looks for GitHub ticket numbers in commit messages.
For every ticket found, we reach out to GitHub REST endpoint to get the issue/pull request data.
Using that data we format and create the release notes.

At the moment, we only load GitHub "pull requests" and ignore GitHub "issues".
This way we can avoid duplication in release notes.
Showing both: the issue closed and the pull request that fixed the issue introduces noise.
Using pull requests will also push teams towards building higher quality software.
We strongly recommend to be disciplined, use pull requests for every change and benefit from peer review and knowledge sharing.

To talk to GitHub REST API Shipkit needs GitHub read-only auth token.
It is very easy and quick to generate on GitHub website.
Then, you only need to configure it in ```shipkit.gradle``` file.

#### User guide

When you made your first release, you will notice where Shipkit puts the release notes by default.
You can configure the location/name of the release notes file:

```gradle
//shipkit.gradle
shipkit {
  releaseNotes.file = "CHANGELOG.md"
}
```

Use pull requests for all code changes!
This builds stronger team, with high visibility of changes and constant peer review.
When merging pull request in GitHub, the default commit message will have #id of the PR.
This is perfect of Shipkit because we can pick up the id from commit message during release notes generation.

How does it sound? Start discussion by opening a ticket in GitHub!

### Automated releases

TBD.

### Avoiding unnecessary releases

See [documentation for ReleaseNeededPlugin](/docs/release-needed-plugin.md).

### Publishing Java libraries

TBD.

### Upgrading dependencies

Check out [documentation for UpgradeDependencyPlugin](/docs/upgrade-dependency-plugin.md).

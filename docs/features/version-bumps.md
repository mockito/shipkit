### Version bumps

See also "[How Shipkit Works](/docs/how-shipkit-works.md)" documentation index.

Yesterday version is already old.
Frequent, automated releases need some way to manage incrementing versions.
Welcome to continuous delivery!

#### Intro

The build needs to be able to automatically use the right version.
This way, you don't need to deal with changing/updating the version for every release.
Every release will produce n+1 version.

There are multiple options how to deal with version bumps.
Some projects automatically infer the version of the next release by looking at the nearest tag in git.
This approach is interesting but it gets complicated when you want to release specific version (like 1.0) rather than rely on the auto-increment.
Shipkit uses a simple ```version.properties``` file that contains the version number.

#### Nitty gritty

Every release, the version file is automatically updated with a new version number.
The next version after "1.0.0" is "1.0.1", then "1.0.2", etc.
If you need to release a specific version, like "1.1.0" or "2.0.0" just update ```version.properties``` file manually and include that in your pull request.

We received feedback that using version file clutters git history with version bump commits.
Since we need to commit changes to release notes / changelog file to the repository, version bumps actually don't introduce any extra commits.
There is already a commit for every release.
Also, we find it useful to see the progression of releases when reviewing git log.
Many times we work with git log for specific source files.
This experience is unaffected by version bumps.
We conclude that high volume of changes to ```version.properties``` is not really that troublesome in practice.

Shipkit uses ```version.properties``` file also for other version related information, like the version of previous release.
It is used for automated generation of release notes.

#### User guide

Shipkit will automatically update the version file on every release so you don't really need to do anything.
Occasionally, you might need to release a more notable version like new major version.
In this case, just update the file manually and commit that change.
Shipkit will build and release the version as specified in the file.

What do you think? Start discussion [by opening a ticket](https://github.com/mockito/shipkit/issues/new) in GitHub!

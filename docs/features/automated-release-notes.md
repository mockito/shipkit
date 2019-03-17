### Automated release notes

See also "[How Shipkit Works](/docs/how-shipkit-works.md)" documentation index.
Please help us with docs and submit a PR with improvements!

Your users need to know what they are getting when upgrading versions.
You want to build great software? Have a great changelog.
You want to build community? Have every contributor listed by name in the release notes.

#### Intro

Shipkit will automatically generate high quality, human readable, well formatted release notes file in markdown.
You only need to include #id of the GitHub ticket number in commit messages.
That happens automatically, if you follow the GitHub pull request model.

Shipkit will motivate you to work in your project with higher craftsmanship:
 - the better descriptions of your GitHub tickets / pull requests -> the better the release notes
 - the more disciplined the team is around using pull requests on daily basis -> the more useful content in release notes

#### Nitty gritty

During the release, Shipkit loads git log and looks for GitHub ticket numbers in commit messages, like "#156".
For every ticket found, we reach out to GitHub REST endpoint to get the issue/pull request data.
Using that data we format and create the release notes.

At the moment, we only load GitHub "pull requests" and ignore GitHub "issues".
This way we can avoid duplication in release notes.
Showing both: the issue closed and the pull request that fixed the issue introduces noise.
Using pull requests will also push teams towards building higher quality software.
We strongly recommend to be disciplined, use pull requests for every change and benefit from peer review and knowledge sharing.

To talk to GitHub REST API Shipkit needs GitHub read-only auth token.
It is very [easy and quick to generate](https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/) the auth token on GitHub.
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
This is perfect of Shipkit because we can pick up the #id from the commit message during release notes generation.

Release notes are also uploaded to GitHub "Releases" page, e.g.: https://github.com/mockito/shipkit/releases

Thank you for reading!
Questions or feedback?
Start discussion [by opening a ticket](https://github.com/mockito/shipkit/issues/new) in GitHub!

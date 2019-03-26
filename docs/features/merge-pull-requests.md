### Feature template

See also "[How Shipkit Works](/docs/how-shipkit-works.md)" documentation index.
Please help us with docs and submit a PR with improvements!

You created a change to your project / other Open Source project. How to merge? 

#### Intro

You created a change but you don't know how to merge it? 
Git allows to use different flows, but what are the best practises of working with Open Source code and Shipkit? 

#### Nitty gritty

The GitHub flow suggest to always create a branch, commit changes to branch and merge when new feature is ready.
We strongly recommended always create Pull Request (PR) and merge using GitHub web interface.
Then it's easier to review changes even using mobile phone.
Committing direct to master branch decrease visibility of the changes.
 
Shipkit (and TravisCI) support controlling some actions during merge of Pull Request. 

#### User guide

When you merge your Pull Request then you can use a few flags in merge messege to control what happens:

- `[ci skip]` - this flag in merge message don't trigger a build. 
This is provided by TravisCI: https://docs.travis-ci.com/user/customizing-the-build/#skipping-a-build
Pull Requests with this flag are not included to release notes.
- `[ci skip-release]` - this flag says that artifacts will be not released. 
Also tag, release notes, version bump etc. will be NOT created/executed.  
- `[ci skip-compare-publications]` - if you need to force release (because of any reason) then you can skip comparing 
publications (current and previous artifact).

If you are using another Continuous Integration server you can configure a flag for NOT triggering a build:

```gradle
shipkit {
   git.commitMessagePostfix = "[ci skip]"
}
``` 

You can also configure with Pull Requests should be NOT included in release notes.
By default it's only `[ci skip]` what make sense, because commits automatically made by Shipkit will be noisy in release notes.
You can define a few flags, e.g.:

```gradle
shipkit {
   releaseNotes.ignoreCommitsContaining = ["[ci skip]", "[ci ignore-in-release-notes]", "fix typo"] 
}
```



Thank you for reading!
Questions or feedback?
Start discussion [by opening a ticket](https://github.com/mockito/shipkit/issues/new) in GitHub!

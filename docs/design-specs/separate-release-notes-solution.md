### Overview

Let's create a small, tailored plugin just for release notes generation (why separate plugin? See #861).
If the plugin is not needed and we can reuse an existing solution, that's OK, too.

### Tactical

Let's create a plugin that can help https://github.com/linkedin/ambry project generate release notes.
One caveat is that Ambry does not want the changelog checked in to the source repository because it prevents concurrent merges (#395).

### Implementation

I suggest that we generate release notes and post them to GitHub releases (no changelog file in the repo).
How do we go about this?

1. we could extract the relevant code from Shipkit itself
2. try to reuse an existing library/plugin out there

We can always do 1) (it's the easiest) however I've started exploring 3) because it gives us an opportunity to simplify Shipkit (see #861).

### Existing release notes solutions

- I looked at existing Gradle plugins that generate release notes.
The only one that seems useful is "git-changelog-gradle-plugin" however it brings too many dependencies (https://github.com/tomasbjerre/git-changelog-gradle-plugin/issues/21).
- I looked at existing non-Gradle solutions and https://github.com/github-changelog-generator/github-changelog-generator works well and has incredible community (6k stars on GH!!!).
It is packaged as a Ruby gem which is relatively easy to setup in Travis CI or GH action.
It does not support posting to GitHub Releases.
- Let's say that we want to try github-changelog-generator, I'm curious if they would be interested in supporting posting to GH releases.
Alternatively we can port the code from Shipkit or find a Gradle plugin that does it well.
The only plugin out there that seems useful is https://github.com/BreadMoirai/github-release-gradle-plugin
(brings ~6 dependencies: https://gist.github.com/mockitoguy/9490306af1bd082b2da78f75228e299d).
Posting release notes to GH seems very simple, perhaps we don't need a plugin but just a simple shell script?
(example: https://gist.github.com/stefanbuck/ce788fee19ab6eb0b4447a85fc99f447)

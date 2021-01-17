# Deprecated

"One Shipkit Gradle plugin to rule them all" approach has proven hard to maintain for the team.
We are converted Shipkit into a narrow set of small libraries ([design note](https://github.com/mockito/shipkit/blob/master/docs/design-specs/future-shipkit.md)).
Several customers, inclusing Mockito project have already migrated.

Helpful links:
 - [Shipkit's vision](https://github.com/shipkit/shipkit-changelog#vision) documented in Shipkit Changelog Plugin
 - [recent migration](https://github.com/linkedin/coral/pull/36) to *new* Shipkit plugins
 - [example customers](https://github.com/shipkit/shipkit-changelog#customers--sample-projects) that already use *new* Shipkit plugins
 - *new* Shipkit plugins documentation:
     - automatic generation of changelog: [shipkit-changelog](https://github.com/shipkit/shipkit-changelog)
     - automatic versioning without *version bump* commits: [shipkit-auto-version](https://github.com/shipkit/shipkit-auto-version)

# Need help?

If you are an existing Shipkit customer and want to use the new plugins, check out the useful links above -> see example projects and example PR that shows how to change the build.gradle files.
If you need help, drop us a ticket in the [shipkit-changelog](https://github.com/shipkit/shipkit-changelog/issues) project.

THANK YOU for considering Shipkit and keep your releases fully automated!

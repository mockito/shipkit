# ReleaseNeededPlugin

Releasing every time we have a change in the project may lead to quite
a lot of releases. It would be better to release only when the change is
interesting from the customer perspective. How to achieve that? Shipkit
provides you with two tasks that may help you with solving that problem:
- **assertReleaseNeeded**
- **releaseNeeded**

The only difference between them is that **assertReleaseNeeded** fails
the build if release is not needed, while **releaseNeeded** gives
you information if release is needed without failing.

## Configuration

Implementations of ReleaseNeededTask allow you to configure following properties:
- **releaseBranchRegex** is used to determine if we should release on this branch or not.
For example we don't want to release on tags and feature branches. This parameter is a
regular Java regex, and defaults to "master|release/.+".
- **pullRequest** is set to **true** if the build is done for pull request.
It is automatically set by Shipkit CI plugins, eg. TravisPlugin.
- **commitMessage** is the message of commit for which build is run.
Also set automatically by Shipkit CI plugins.
- **branch** is a current Git branch. Also set by Shipkit CI Plugins.


## Reasons why release may not be needed

There is a couple of them:
- Environment variable **SKIP_RELEASE** is set to any value (eg. 'true')
- Commit message contains "[ci skip-release]"
- The build is a pull request, see [Travis Documentation](https://docs.travis-ci.com/user/environment-variables/) for more information
- Branch is a releasable one, see **releaseBranchRegex** in [Configuration section](#configuration)
- One of the publications changed, see [section below](#comparing-publications)

## Usage

You usually don't need to apply this plugin. It comes with **ShipkitJavaPlugin**
or **ShipkitGradlePlugin**. Also **assertReleaseNeeded** is a part of **ciPerformRelease** task.

To test it - run in the command line:

```
./gradlew assertReleaseNeeded
```

or

```
./gradlew releaseNeeded
```


## Comparing publications

TBD.

# Upgrade dependency plugin

Note that this plugin is incubating!

See also "[How Shipkit Works](/docs/how-shipkit-works.md)" documentation index.
Help us with docs and submit a PR if something is amiss!

Let's say we have two projects **child** and **parent**. **Child** depends on **parent**. At one point **parent** releases a new version, and dependency on it in **child** needs to be updated. One or two times you can update this dependency manually, but the more often it happens the more manual work you have. What happens if you have more of parent projects? You need to update a lot of dependencies. Even more manual work.

UpgradeDependencyPlugin is aiming at solving these problems by automating the process.

## Configuration

You use it by configuring your shipkit.gradle file like that:

```
apply plugin: 'org.shipkit.upgrade-dependency'

upgradeDependency {
   baseBranch = 'release/2.x'
   buildFile = file('build.gradle')
}
```

where:
- **baseBranch** - Git branch in **child** project to which we want to create a pull request with version upgrade. Defaults to "master".
- **buildFile** - file in which Shipkit will be able to find the dependency on **parent** and replace the version with the new one. Its type is java.io.File and defaults to file('build.gradle')

## Usage

Now when you have the plugin configured, you can go to your command line,  open **child** root dir and run:

>./gradlew performVersionUpgrade -Pdependency=**parentGroup**:**parentArtifactName**:**parentVersion**

where:
- **parentGroup** is a group of **parent** project, eg. "org.shipkit".
- **parentArtifactName** is the name of the **parent** artifact, eg. "shipkit"
- **newParentVersion** is the new version of **parent**

Project property **dependency** is used to replace the version of **parent** in **buildFile** of **child**. At the moment we assume that **parentVersion** can only contain digits and dots. So we are looking in **buildFile** for a string matching the pattern:
${parentGroup}:${parentName}:[0-9.]+
and replacing version in the result with **newParentVersion**.

Executing task **performVersionUpgrade** actually does quite a few things:
1. Checkouts the base branch (usually master).
2. Performs a git pull to the **baseBranch** to keep the code up-to-date and minimise the risk of conflicts. Repository URL contains authentication data if you have you GHWriteToken configured.
3. Creates a new branch, let's call it **versionBranch**. It is actually named like this: "upgrade-**parent**-to-**newParentVersion**".
4. Updates **parent** version using the pattern specified in **dependency** project property.
5. Commits the changes.
6. Performs git push to  **versionBranch**.
7. Creates a pull request from **versionBranch** to **baseBranch**.

If the task succeeds the only thing for you to do is wait for all GitHub checks to pass and merge it!

Want to know more? See [the code](https://github.com/mockito/shipkit/blob/master/subprojects/shipkit/src/main/groovy/org/shipkit/internal/gradle/versionupgrade/UpgradeDependencyPlugin.java)! Whole plugin configuration is there. Note that each mentioned step of **performVersionUpgrade** is a separate task and can be used on its own.

Thank you for reading!
Questions or feedback?
Start discussion [by opening a ticket](https://github.com/mockito/shipkit/issues/new) in GitHub!

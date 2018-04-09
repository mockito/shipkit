# Problem

Currently, we rely on external https://javadoc.io service to host viewable Javadoc.
When we publish the jars with javadocs to Maven Central, the javadoc is automatically downloaded, unzipped and hosted at javadoc.io.
This works well for projects that ship to Central.
Projects that don't ship to Central (some project ship only to https://bintray.com) don't get this benefit.
Best example is Shipkit project itself - currently we don't host unzipped Javadocs that are easy to view online.

## Design

We can use [GitHub Pages](https://pages.github.com/) to host Javadoc.
A project that uses Shipkit already keeps source code in GitHub.
Hence, we stay with the same toolchain if we host Javadoc with GitHub.

Every time we release, we will write Javadoc html to the Javadoc repository.
It is recommended to create separate repo for Javadocs rather than writing html to a branch on the main source repo.
This way, we keep the source repo smaller, faster to clone and work with.

To try out GitHub pages, I pushed Shipkit 2.0.17 javadoc to 2 repos:
 - https://github.com/mockito/shipkit-javadoc, view at: http://site.mockito.org/shipkit-javadoc/shipkit/current/index.html
 - https://github.com/shipkit/javadoc, view at: https://shipkit.github.io/javadoc/shipkit/current/index.html

## Repo layout

Using Mockito project as example, it ships multiple jars, "mockito-core", "mockito-android", and such.

```
mockito-core/1.0.0/index.html
mockito-core/1.0.0/...
mockito-core/1.0.1/index.html
mockito-core/1.0.1/...
mockito-core/current/index.html
mockito-core/current/...
mockito-core/current/index.html
mockito-android/...
```

Repo layout should support:
 - submodules - a project can produce multiple jars, each can have separate Javadoc
 - "current" Javadoc - a place where you can view the Javadoc for the latest version

```
[submodule1]/[version]/[files]
[submodule1]/current/[files]
[submodule2]/[version]/[files]
[submodule2]/current/[files]
```

In the future, we can have separate "current" directory for a major version, e.g. "current-2.x", "current-3.x"

## Implementation

(Work in progress)

- new plugin: “org.shipkit.javadoc"
- behavior:
    - clone Javadoc repo (shallow clone for speed!)
    - copy new javadoc files to a new version directory
    - refresh files in "current" directory
    - git add, git commit, git push
    - announce
        - the message at the end of the release points to new javadoc
        - release notes contain link to new javadoc


### Pseudo code

Pseudo code design of “org.shipkit.javadoc" (work in progress)

```
workDir = build/shipkit-javadoc
repoDir = workDir/javadoc-repo
stagingDir = workDir/javadoc-staging

tasks.create "cloneJavadocRepo" {
	cloneRepo = conf.gitHub.repo + "-javadoc"
	targetDir = repoDir
	onlyIf { stagingDir not empty }
}

allCopyTasks = []
allprojects.plugins.withId("shipkit.java.library") {
	//configure name based on
	allCopyTasks << tasks.create("copyReleaseJavadoc", Copy) {
		dependsOn javadocJar
		cloneJavadocRepo.mustRunAfter it //build javadoc first so that if there is no javadoc we will avoid cloning
		from { zipTree(javadocJar.archivePath) } // we publish javadoc as in the javadoc jar file to force consistency
		into stagingDir/javadocJar.baseName
	}
}

task refreshCurrentJavadoc(type: Sync) {
	mustRunAfter cloneJavadocRepo
	dependsOn allCopyTasks
	from stagingDir
	into $cloneRepo/current
	onlyIf { stagingDir not empty }
}

task refreshVersionJavadoc(type: Sync) {
	mustRunAfter cloneJavadocRepo
	dependsOn allCopyTasks
	from stagingDir
	into $cloneRepo/$project.version
	onlyIf { stagingDir not empty }
}

task gitCommitJavadoc(type: GitCommitTask) {
	workDir = repoDir
	//below makes this task depend on refresh* tasks
	addChange($cloneRepo/current, refreshCurrentJavadoc)
	addChange($cloneRepo/$project.version, refreshVersionJavadoc)
}

task gitPushJavadoc(type: GitPushTask) {
	mustRunAfter gitCommitJavadoc
	deferredConfiguration {
		dryRun = conf.dryRun
		secretValue = conf.gitHub.writeToken
	}
}

task releaseJavadoc {
	dependsOn cloneJavadocRepo
	dependsOn gitCommitJavadoc //already depends on javadoc producing tasks
	dependsOn gitPushJavadoc
}

project.getPlugins().withType(ReleasePlugin.class) {
  performRelease.dependsOn releaseJavadoc
}
```

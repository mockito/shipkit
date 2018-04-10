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

- new plugin: “org.shipkit.javadoc"
- the plugin adds tasks that
    - clone Javadoc repo (shallow clone for speed!)
    - depend on "javadocJar" tasks of type Jar (convention over configuration:
    if you have a task named "javadocJar" of type Jar, the plugin picks it up automatically.
    - unzip the contents of "javadocJar" and copy them to javadoc repo
    Unzipping the jar optimizes for correctness, not for speed.
    We want to guaratee that the published javadoc html is _exactly_ what in the jar file
    - git add, git commit, git push
    - announce
        - the message at the end of the release points to new javadoc
        - release notes contain link to new javadoc

### Example implementation

Example implementation is for guidance only.

New “org.shipkit.javadoc" plugin

```
workDir = build/shipkit-javadoc      //parent dir for repo dir and for staging dir
repoDir = workDir/javadoc-repo       //clone dir of the GH javadoc repo
stagingDir = workDir/javadoc-staging //where we prepare files

tasks.create "cloneJavadocRepo" {
    description "Clones Javadoc repository where we store html files"

	cloneRepo = conf.gitHub.repo + "-javadoc" //sensible default (convention over configuration)
	targetDir = repoDir
	onlyIf { stagingDir not empty }
	// TODO shallow clone!
}

task refreshVersionJavadoc(type: Sync) {
    description "Copies Javadoc html from staging dir to the javadoc repo clone dir"

	mustRunAfter cloneJavadocRepo
	from stagingDir
	into $cloneRepo/$project.version
	onlyIf { stagingDir not empty }
}

task refreshCurrentJavadoc(type: Sync) {
    description "Copies Javadoc html from javadoc repo clone dir "[version]" dir to "current" dir"

	dependsOn refreshVersionJavadoc
	from $cloneRepo/$project.version
	into $cloneRepo/current
	onlyIf { $cloneRepo/$project.version not empty }
}

allprojects.tasks.matching { it.name == 'javadocJar' && it instanceof Jar) }.all { task ->
	//convention over configuration: pick up all 'javadocJar' tasks of type Jar
	tasks.create("copyContentsOf" + task.name, Copy) {
	    description "Extracts contents of javadoc jar to the staging directory"

		dependsOn task
		refreshVersionJavadoc dependsOn it
		cloneJavadocRepo.mustRunAfter it //build javadoc first so that if there is no javadoc we will avoid cloning
		from { zipTree(javadocJar.archivePath) }
		into { stagingDir/javadocJar.baseName } // note that we need to use Closure/Callable because 'baseName' can be set by user later
	}
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

Existing ReleasePlugin
    - apply JavadocPlugin
    - performRelease.dependsOn releaseJavadoc
```

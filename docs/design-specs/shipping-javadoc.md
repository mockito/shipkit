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

## Repo layout

```
mockito-core/1.0.0/index.html
mockito-core/1.0.0/...
mockito-core/1.0.1/index.html
mockito-core/1.0.1/...
mockito-core/current/index.html
mockito-core/current/...
mockito-core/current/index.html

org.mockito
```




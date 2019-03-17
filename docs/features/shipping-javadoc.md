### Shipping Javadoc

See also "[How Shipkit Works](/docs/how-shipkit-works.md)" documentation index.
Please help us with docs and submit a PR with improvements!

Your users need to know documentation of your project. You want release documentation? 
Write it as Javadoc and publish to separate (or the same) repo. Make it available for everyone. 

#### Intro

Gradle build generate Jar with Javadoc by default. Shipkit allows you to publish your Javadoc in GitHub repository.
In connection with [GitHub Pages](https://pages.github.com/) your documentation can be visible for your users right 
after each release. 

#### Nitty gritty

We are recommending to create separate repository for your project, e.g.: yourOrg/yourProject-javadoc and publish 
Javadoc there. 
Or if you use GitHub Pages then you can publish Javadocs to your Project GitHub Page.  
There is also a possibility to publish Javadocs to the same repository, but in that case your repo will grow very fast.
 

#### User guide

To enable shipping Javadoc, you need to apply Javadoc plugin in your ```build.gradle``` in root project:

```gradle
apply plugin: "org.shipkit.java"
// enable Shipkit Javadoc plugin. Now Javadoc can be shipped to the GitHub repository
apply plugin: "org.shipkit.javadoc"
``` 

Now you can configure your Javadoc repository in ```gradle/shipkit.gradle```:

```gradle
shipkit {
    gitHub.repository = "mockito/shipkit-example"
    // ...
    
    // OPTIONAL GitHub repository (owner/repo) where Javadocs are shipped.
    // To enable shipping Javadoc you need to apply Javadoc plugin first:
    // apply plugin: "org.shipkit.javadoc"
    // The default value is gitHub.repository + "-javadoc", so in this project case:
    // https://github.com/mockito/shipkit-example-javadoc
    javadoc.repository = "mockito/shipkit-example-javadoc"
    
    // OPTIONAL GitHub Javadoc repository branch name. The branch needs to exist.
    // By default it's using the branch set as main in GitHub repo, usually master.
    // To enable shipping Javadoc you need to apply Javadoc plugin first:
    // apply plugin: "org.shipkit.javadoc"
    javadoc.repositoryBranch = "gh-pages"
    
    // OPTIONAL GitHub Javadoc repository directory where put javadoc files. By default it's project root directory.
    // To enable shipping Javadoc you need to apply Javadoc plugin first:
    // apply plugin: "org.shipkit.javadoc"
    javadoc.repositoryDirectory = "dir1/dir2"
    
    // OPTIONAL Commit message used to commit Javadocs. Default: "Update current and ${version} Javadocs. [ci skip]"
    // You can override this message and ${version} will be replaced by currently build version.
    // You don't need to specify "[ci skip]" in your message - it will be added automatically.
    // To enable shipping Javadoc you need to apply Javadoc plugin first:
    // apply plugin: "org.shipkit.javadoc"
    javadoc.commitMessage = "Update current and ${version} Javadocs."
}
``` 

Thank you for reading!
Questions or feedback?
Start discussion [by opening a ticket](https://github.com/mockito/shipkit/issues/new) in GitHub!

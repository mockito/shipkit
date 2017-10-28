### Publishing binaries

See also "[How Shipkit Works](/docs/how-shipkit-works.md)" documentation index.
Please help us with docs and submit a PR with improvements!

You have the code but your users need compiled and tested binaries so that they can use your code!

#### Intro

Shipkit will automatically publish binaries to [Bintray](https://bintray.com) repository.
It is easy to publish your binaries to [Bintray's JCenter](https://jcenter.bintray.com/) and sync to [Maven Central](http://central.sonatype.org/).
The details of the repository is configurable in "shipkit.gradle" file.
Shipkit supports publishing multiple artifacts during single release.
For example, [Mockito project publishes](http://search.maven.org/#search%7Cga%7C1%7Corg.mockito) "mockito-core", "mockito-android" and "mockito-inline" artifacts during each release.

#### Nitty gritty

Applying "[org.shipkit.java](https://plugins.gradle.org/plugin/org.shipkit.java)" Gradle plugin will automatically configure Shipkit to publish all jars built in given Gradle project.
The only thing left is configuring Bintray Gradle plugin.
See the example in the next section how we configure Bintray.

Note that we use env variables for sensitive API keys and tokens.
This is the safest way to handle them in CI environment.
We don't want to check in.
Every CI system, including Travis CI, offers convenient ways to safely configure env variables.

The publication is automatically triggered when one runs one of the following Gradle tasks.
Useful for local testing:

```
./gradlew testRelease
./gradlew performRelease -PdryRun
```

All tasks:

```
./gradlew bintrayUpload
./gradlew performRelease
./gradlew ciPerformRelease
```

Normally, we don't run any of those tasks.
Travis CI is configured to publish automatically but only [when the release is needed](docs/gradle-plugins/release-needed-plugin.md).
Here's an example [.travis.yml file](https://github.com/mockito/shipkit-example/blob/master/.travis.yml) from our example project.

```
script:
  - ./gradlew build && ./gradlew ciPerformRelease
```

#### User guide

Below is working "[shipkit.gradle](https://github.com/mockito/shipkit-example/blob/master/gradle/shipkit.gradle)" file from our example project.

Configuring how binaries are published relies on [JFrog Bintray Gradle plugin](https://github.com/bintray/gradle-bintray-plugin).
It is a separate OSS project, owned by JFrog and hosted on GitHub.

```gradle
//shipkit.gradle
shipkit {
    gitHub.repository = 'mockito/shipkit-example'
    gitHub.readOnlyAuthToken = 'e7fe8fcdd6ffed5c38498c4c79b2a68e6f6ed1bb'
    gitHub.writeAuthToken = System.getenv('GH_WRITE_TOKEN')
    team.developers = ['mockitoguy:Szczepan Faber', 'mstachniuk:Marcin Stachniuk', 'wwilk:Wojtek Wilk']
}

allprojects {
    plugins.withId('com.jfrog.bintray') {

        //Bintray configuration is handled by JFrog Bintray Gradle Plugin
        //See the official documentation: https://github.com/bintray/gradle-bintray-plugin
        bintray {

            //We keep the key hidden behind an environmental variable
            //That variable is configured in Travis CI repository settings
            //To see your API key, sign up for free account at https://bintray.com and navigate to your profile
            key = System.getenv('BINTRAY_API_KEY')

            pkg {
                repo = 'examples'
                user = 'mockitoguy'
                userOrg = 'shipkit'
                name = 'basic'
                licenses = ['MIT']
                labels = ['continuous delivery', 'release automation', 'mockito', 'shipkit']
            }
        }
    }
}
```

#### Shipping to Maven Central

To publish to Maven Central, please [include your Bintray repository in JCenter](TODO) and configure [Bintray's Gradle plugin](https://github.com/bintray/gradle-bintray-plugin) accordingly.

We use env variables to manage secret Nexus credentials, needed for publication to Maven Central.

```gradle
//shipkit.gradle
shipkit {

    // (...)

}

allprojects {
    plugins.withId('com.jfrog.bintray') {
        bintray {
            pkg {

                // (...)

                version {
                    mavenCentralSync {
                        sync = true
                        user = System.env.NEXUS_TOKEN_USER
                        password = System.env.NEXUS_TOKEN_PWD
                    }
                }
            }
        }
    }
}
```

Thank you for reading!
Questions or feedback?
Start discussion [by opening a ticket](https://github.com/mockito/shipkit/issues/new) in GitHub!

## Plugin hierarchy

### org.shipkit.java

Below hierarchy shows how plugins apply other plugins, starting for top-level "org.shipkit.java".
The documentation is as of July 2017 and might get out of date :) We will see.

- java (continuous delivery for typical Java project with Bintray and Travis)
  - [to all “java” submodules] java-bintray (publishing java modules using bintray)
    - bintray
      - com.jfrogs.bintray
    - java-publish (publishing with maven-publish)
      - java-library (sources & javadoc jar)
        - java
      - maven-publish
  - pom-contributors (contributiors in pom file)
    - contributors (gets contributors for pom)
  - bintray-release (publish with Bintray)
    - release
      - release-notes (generating release notes)
        - versioning (version bumps, loading versions)
        - contributors
      - git (git commit, tag, push)
      - release-needed (checking if release is needed)
  - travis (cd with Travis CI)
    - ci-release (releasing from CI system)
      - release (*)
    - git-setup (git checkout, unshallow, git user)
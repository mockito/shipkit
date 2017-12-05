package org.shipkit.internal.util

import spock.lang.Specification

import static org.shipkit.internal.util.RepositoryNameUtil.*

class RepositoryNameUtilTest extends Specification {

    def "should convert to camelCase"(String repoName, String camelCase, String capitalizedCamelCase) {
        expect:
        camelCase == repositoryNameToCamelCase(repoName)
        capitalizedCamelCase == repositoryNameToCapitalizedCamelCase(repoName)

        where:
        repoName                  | camelCase               | capitalizedCamelCase
        "mockito/shipkit-example" | "mockitoShipkitExample" | "MockitoShipkitExample"
        "wwilk/shipkit"           | "wwilkShipkit"          | "WwilkShipkit"
        "org/repo_name"           | "orgRepoName"           | "OrgRepoName"
    }

    def "should extract repo name from url"() {
        expect:
        "mockito/shipkit" == extractRepoNameFromGitHubUrl("https://github.com/mockito/shipkit")
        "mockito/shipkit.git" == extractRepoNameFromGitHubUrl("https://github.com/mockito/shipkit.git")
    }
}

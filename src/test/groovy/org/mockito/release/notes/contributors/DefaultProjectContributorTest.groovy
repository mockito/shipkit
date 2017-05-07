package org.mockito.release.notes.contributors

import spock.lang.Specification

class DefaultProjectContributorTest extends Specification {

    def "equals"() {
        def contributor = new DefaultProjectContributor(
                "Szczepan Faber", "szczepiq", "http://github.com/szczepiq", 1)
        def same = new DefaultProjectContributor(
                "Szczepan Faber", "szczepiq", "http://github.com/szczepiq", 1)
        def differentName = new DefaultProjectContributor(
                "xxx", "szczepiq", "http://github.com/szczepiq", 1)
        def differentLogin = new DefaultProjectContributor(
                "Szczepan Faber", "xxx", "http://github.com/szczepiq", 1)
        def differentUrl = new DefaultProjectContributor(
                "Szczepan Faber", "szczepiq", "xxx", 1)
        def differentContributions = new DefaultProjectContributor(
                "Szczepan Faber", "szczepiq", "http://github.com/szczepiq", 100)

        expect:
        contributor == same
        same == same
        contributor == differentContributions

        contributor != differentName
        contributor != differentLogin
        contributor != differentUrl
    }
}

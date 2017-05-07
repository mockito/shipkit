package org.mockito.release.notes.contributors

import spock.lang.Specification

class DefaultProjectContributorsSetTest extends Specification {

    def "knows if has contributor"() {
        def set = new DefaultProjectContributorsSet()
        set.addContributor(new DefaultProjectContributor(
                "Szczepan Faber", "szczepiq", "http://github.com/szczepiq", 2000))

        set.addContributor(new DefaultProjectContributor(
                "John Doe", "jdoe", "http://github.com/jdoe", 10))

        expect:
        //positive cases
        set.allContributors.contains(new DefaultProjectContributor("Szczepan Faber", "szczepiq", "http://github.com/szczepiq", 2000))
        set.allContributors.contains(new DefaultProjectContributor("Szczepan Faber", "szczepiq", "http://github.com/szczepiq", 10))

        //negative cases
        !set.allContributors.contains(new DefaultProjectContributor("Szczepan Faber x", "szczepiq", "http://github.com/szczepiq", 2000))
        !set.allContributors.contains(new DefaultProjectContributor("Szczepan Faber", "szczepiqx", "http://github.com/szczepiq", 2000))
        !set.allContributors.contains(new DefaultProjectContributor("Szczepan Faber", "szczepiq", "http://github.com/szczepiqx", 2000))
    }

    def "does not drop contributors with the same amount of contributions"() {
        def set = new DefaultProjectContributorsSet()
        set.addContributor(new DefaultProjectContributor(
                "Szczepan Faber 1", "szczepiq", "http://github.com/szczepiq", 2000))
        set.addContributor(new DefaultProjectContributor(
                "Szczepan Faber 2", "szczepiq", "http://github.com/szczepiq", 2000))
        set.addContributor(new DefaultProjectContributor(
                "Szczepan Faber 3", "szczepiq", "http://github.com/szczepiq", 2000))

        expect:
        set.allContributors.size() == 3
    }
}

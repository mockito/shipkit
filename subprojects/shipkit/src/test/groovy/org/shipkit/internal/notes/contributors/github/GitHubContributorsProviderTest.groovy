package org.shipkit.internal.notes.contributors.github

import org.shipkit.internal.notes.contributors.DefaultContributor
import org.shipkit.internal.notes.contributors.DefaultProjectContributor
import org.shipkit.internal.notes.contributors.DefaultProjectContributorsSet
import spock.lang.Specification

import static org.shipkit.internal.notes.contributors.github.GitHubContributorsProvider.mergeContributors

class GitHubContributorsProviderTest extends Specification {

    def "merges contributors"() {
        def set = new DefaultProjectContributorsSet()
        set.addContributor(new DefaultProjectContributor("a", "a", "a", 10))
        set.addContributor(new DefaultProjectContributor("b", "b", "b", 20))

        def recent = [
                new DefaultContributor("b", "b", "b"), //will not overwrite
                new DefaultContributor("c", "c", "c") //will be added
        ]
        when:
        def result = mergeContributors(set, recent)

        then:
        //sorted, the initial 'b' contributor was not overwritten
        result.allContributors == [
            new DefaultProjectContributor("b", "b", "b", 20),
            new DefaultProjectContributor("a", "a", "a", 10),
            new DefaultProjectContributor("c", "c", "c", 1)
        ] as LinkedHashSet
    }
}

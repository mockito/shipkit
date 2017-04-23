package org.mockito.release.notes.contributors

import org.mockito.release.notes.model.Contributor
import spock.lang.Specification
import spock.lang.Subject

class ContributorsSerializerTest extends Specification {

    File tempFile = new File(System.getProperty("java.io.tmpdir") + "/contributors.json")
    @Subject serializer = new ContributorsSerializer(tempFile)

    def "serialization and deserialization of one contributor"() {
        given:
        def contributors = new DefaultContributorsSet<Contributor>()
        def contributor = new DefaultContributor("myName", "myLogin", "myProfileUrl")
        contributors.addContributor(contributor)

        when:
        serializer.serialize(contributors)
        def actual = serializer.deserialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization of zero contributors"() {
        given:
        def contributors = new DefaultContributorsSet<Contributor>()

        when:
        serializer.serialize(contributors)
        def actual = serializer.deserialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
        actual.size() == 0
    }

    def "serialization and deserialization of 2 contributors"() {
        given:
        def contributors = new DefaultContributorsSet<Contributor>()
        def contributor1 = new DefaultContributor("myName 1", "myLogin 1", "myProfileUrl 1")
        def contributor2 = new DefaultContributor("myName 2", "myLogin 2", "myProfileUrl 2")
        contributors.addContributor(contributor1)
        contributors.addContributor(contributor2)

        when:
        serializer.serialize(contributors)
        def actual = serializer.deserialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization contributors with special characters"() {
        given:
        def contributors = new DefaultContributorsSet<Contributor>()
        def contributor = new DefaultContributor("my\"Na\\m\fe ", "my\rLo\bg\ni\tn", "\u0000myP\u001Fro\u007Ffi\u009FleUrl ")
        contributors.addContributor(contributor)

        when:
        serializer.serialize(contributors)
        def actual = serializer.deserialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }


}

package org.mockito.release.notes.contributors

import spock.lang.Specification
import spock.lang.Subject

class ContributorsSerializerTest extends Specification {

    @Subject serializer = new ContributorsSerializer(System.getProperty("java.io.tmpdir") + "/contributors.json")
    def writer = new StringWriter()

    def "serialization and deserialization of one contributor"() {
        given:
        def contributors = new DefaultContributorsSet()
        def contributor = new DefaultContributor("myName", "myLogin", "myProfileUrl")
        contributors.addContributor(contributor)

        when:
        serializer.serialize(contributors)
        def actual = serializer.desrialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization of zero contributors"() {
        given:
        def contributors = new DefaultContributorsSet()

        when:
        serializer.serialize(contributors)
        def actual = serializer.desrialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization of 2 contributors"() {
        given:
        def contributors = new DefaultContributorsSet()
        for (int i = 0; i < 2; i++) {
            def contributor = new DefaultContributor("myName " + i, "myLogin" + i, "myProfileUrl " + i)
            contributors.addContributor(contributor)
        }

        when:
        serializer.serialize(contributors)
        def actual = serializer.desrialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization of 5 contributors"() {
        given:
        def contributors = new DefaultContributorsSet()
        for (int i = 0; i < 5; i++) {
            def contributor = new DefaultContributor("myName " + i, "myLogin" + i, "myProfileUrl " + i)
            contributors.addContributor(contributor)
        }

        when:
        serializer.serialize(contributors)
        def actual = serializer.desrialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization contributors with special characters"() {
        given:
        def contributors = new DefaultContributorsSet()
        def contributor = new DefaultContributor("my\"Na\\m\fe ", "my\rLo\bg\ni\tn", "\u0000myP\u001Fro\u007Ffi\u009FleUrl ")
        contributors.addContributor(contributor)

        when:
        serializer.serialize(contributors)
        def actual = serializer.desrialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }


}

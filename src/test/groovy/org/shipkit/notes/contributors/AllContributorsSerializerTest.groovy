package org.shipkit.notes.contributors

import spock.lang.Specification
import spock.lang.Subject

class AllContributorsSerializerTest extends Specification {

    @Subject serializer = new AllContributorsSerializer()

    def "serialization and deserialization of one contributor"() {
        def contributors = new DefaultProjectContributorsSet()
        def contributor = new DefaultProjectContributor("myName", "myLogin", "myProfileUrl", 5)
        contributors.addContributor(contributor)

        when:
        def serializeData = serializer.serialize(contributors)
        def actual = serializer.deserialize(serializeData)

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization of zero contributor"() {
        def contributors = new DefaultProjectContributorsSet()

        when:
        def serializedData = serializer.serialize(contributors)
        def actual = serializer.deserialize(serializedData)

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
        actual.size() == 0
    }

    def "serialization and deserialization of two contributors"() {
        def contributors = new DefaultProjectContributorsSet()
        def contributor1 = new DefaultProjectContributor("myName 1", "myLogin 1", "myProfileUrl 1", 5)
        def contributor2 = new DefaultProjectContributor("myName 2", "myLogin 2", "myProfileUrl 2", 5)
        contributors.addContributor(contributor1)
        contributors.addContributor(contributor2)

        when:
        def serializedData = serializer.serialize(contributors)
        def actual = serializer.deserialize(serializedData)

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization contributor with special characters"() {
        def contributors = new DefaultProjectContributorsSet()
        def contributor = new DefaultProjectContributor("my\"Na\\m\fe ", "my\rLo\bg\ni\tn", "\u0000myP\u001Fro\u007Ffi\u009FleUrl ", 5)
        contributors.addContributor(contributor)

        when:
        def serializedData = serializer.serialize(contributors)
        def actual = serializer.deserialize(serializedData)

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization contributor with emty name"() {
        def contributors = new DefaultProjectContributorsSet()
        def contributor = new DefaultProjectContributor("", "myLogin", "myProfileUrl", 5)
        contributors.addContributor(contributor)

        when:
        def serializedData = serializer.serialize(contributors)
        def actual = serializer.deserialize(serializedData)

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization contributor with zero contributions"() {
        def contributors = new DefaultProjectContributorsSet()
        def contributor = new DefaultProjectContributor("myName", "myLogin", "myProfileUrl", 0)
        contributors.addContributor(contributor)

        when:
        def serializedData = serializer.serialize(contributors)
        def actual = serializer.deserialize(serializedData)

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }
}

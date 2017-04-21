package org.mockito.release.notes.contributors

import org.mockito.release.notes.model.ProjectContributor
import spock.lang.Specification
import spock.lang.Subject

class AllContributorsSerializerTest extends Specification {

    private File tempFile = new File(System.getProperty("java.io.tmpdir") + "/project-contributors.json")
    @Subject serializer = new AllContributorsSerializer(tempFile)

    def "serialization and deserialization of one contributor"() {
        def contributors = new DefaultProjectContributorsSet()
        def contributor = new DefaultProjectContributor("myName", "myLogin", "myProfileUrl", 5)
        contributors.addContributor(contributor)

        when:
        serializer.serialize(contributors)
        def actual = serializer.desrialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization of zero contributor"() {
        def contributors = new DefaultProjectContributorsSet()

        when:
        serializer.serialize(contributors)
        def actual = serializer.desrialize()

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
        serializer.serialize(contributors)
        def actual = serializer.desrialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization of 5 contributors"() {
        def contributors = new DefaultProjectContributorsSet()
        def contributor1 = new DefaultProjectContributor("myName 1", "myLogin 1", "myProfileUrl 1", 5)
        def contributor2 = new DefaultProjectContributor("myName 2", "myLogin 2", "myProfileUrl 2", 6)
        def contributor3 = new DefaultProjectContributor("myName 3", "myLogin 3", "myProfileUrl 3", 7)
        def contributor4 = new DefaultProjectContributor("myName 4", "myLogin 4", "myProfileUrl 4", 8)
        def contributor5 = new DefaultProjectContributor("myName 5", "myLogin 5", "myProfileUrl 5", 9)
        contributors.addContributor(contributor1)
        contributors.addContributor(contributor2)
        contributors.addContributor(contributor3)
        contributors.addContributor(contributor4)
        contributors.addContributor(contributor5)

        when:
        serializer.serialize(contributors)
        def actual = serializer.desrialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization contributor with special characters"() {
        def contributors = new DefaultProjectContributorsSet()
        def contributor = new DefaultProjectContributor("my\"Na\\m\fe ", "my\rLo\bg\ni\tn", "\u0000myP\u001Fro\u007Ffi\u009FleUrl ", 5)
        contributors.addContributor(contributor)

        when:
        serializer.serialize(contributors)
        def actual = serializer.desrialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization contributor with emty name"() {
        def contributors = new DefaultProjectContributorsSet()
        def contributor = new DefaultProjectContributor("", "myLogin", "myProfileUrl", 5)
        contributors.addContributor(contributor)

        when:
        serializer.serialize(contributors)
        def actual = serializer.desrialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }

    def "serialization and deserialization contributor with zero contributions"() {
        def contributors = new DefaultProjectContributorsSet()
        def contributor = new DefaultProjectContributor("myName", "myLogin", "myProfileUrl", 0)
        contributors.addContributor(contributor)

        when:
        serializer.serialize(contributors)
        def actual = serializer.desrialize()

        then:
        actual.getAllContributors().containsAll(contributors.getAllContributors())
        contributors.getAllContributors().containsAll(actual.getAllContributors())
    }
}
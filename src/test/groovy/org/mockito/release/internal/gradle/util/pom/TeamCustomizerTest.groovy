package org.mockito.release.internal.gradle.util.pom

import org.mockito.release.notes.contributors.DefaultProjectContributor
import spock.lang.Specification
import spock.lang.Subject

class TeamCustomizerTest extends Specification {

    @Subject sut = new TeamCustomizer()

    def "should include all contributors from contributors set"() {
        def node = new Node(null, "contributors")
        def contributorsSet = [new DefaultProjectContributor("name 1", "login1", "profileUrl1", 1),
                               new DefaultProjectContributor("name 2", "login2", "profileUrl2", 1)
        ] as Set
        def contributorsStringList = []
        def developers = []

        when:
        sut.include(node, contributorsSet, contributorsStringList, developers)

        then:
        node.children().get(0).get("name").text() == "name 1"
        node.children().get(0).get("url").text() == "profileUrl1"
        node.children().get(1).get("name").text() == "name 2"
        node.children().get(1).get("url").text() == "profileUrl2"
    }

    def "should include all contributors from contributors string list"() {
        def node = new Node(null, "contributors")
        def contributorsSet = [ ] as Set
        def contributorsStringList = ["login1:name 1", "login2:name 2"]
        def developers = []

        when:
        sut.include(node, contributorsSet, contributorsStringList, developers)

        then:
        node.children().get(0).get("name").text() == "name 1"
        node.children().get(0).get("url").text() == "https://github.com/login1"
        node.children().get(1).get("name").text() == "name 2"
        node.children().get(1).get("url").text() == "https://github.com/login2"
    }

    def "should ignore contributor from set when it is a developer but check only login"() {
        def node = new Node(null, "contributors")
        def contributorsSet = [new DefaultProjectContributor("name 1", "login1", "profileUrl1", 1),
                               new DefaultProjectContributor("name 2", "login2", "profileUrl2", 1)] as Set
        def contributorsStringList = []
        def developers = ["login1:different name"]

        when:
        sut.include(node, contributorsSet, contributorsStringList, developers)

        then:
        node.children().get(0).get("name").text() == "name 2"
        node.children().get(0).get("url").text() == "profileUrl2"
    }

    def "should never ignore contributor from string even it is a developer"() {
        def node = new Node(null, "contributors")
        def contributorsSet = [new DefaultProjectContributor("name 1", "login1", "profileUrl1", 1),
                               new DefaultProjectContributor("name 2", "login2", "profileUrl2", 1)] as Set
        def contributorsStringList = ["login3:name 3"]
        def developers = ["login3:name 3"]

        when:
        sut.include(node, contributorsSet, contributorsStringList, developers)

        then:
        node.children().get(0).get("name").text() == "name 3"
        node.children().get(0).get("url").text() == "https://github.com/login3"
        node.children().get(1).get("name").text() == "name 1"
        node.children().get(1).get("url").text() == "profileUrl1"
        node.children().get(2).get("name").text() == "name 2"
        node.children().get(2).get("url").text() == "profileUrl2"
    }

    def "should always put contributors from string before from github"() {
        def node = new Node(null, "contributors")
        def contributorsSet = [new DefaultProjectContributor("name 1", "login1", "profileUrl1", 1),
                               new DefaultProjectContributor("name 2", "login2", "profileUrl2", 1)] as Set
        def contributorsStringList = ["login3:name 3"]
        def developers = []

        when:
        sut.include(node, contributorsSet, contributorsStringList, developers)

        then:
        node.children().get(0).get("name").text() == "name 3"
        node.children().get(0).get("url").text() == "https://github.com/login3"
        node.children().get(1).get("name").text() == "name 1"
        node.children().get(1).get("url").text() == "profileUrl1"
        node.children().get(2).get("name").text() == "name 2"
        node.children().get(2).get("url").text() == "profileUrl2"
    }

}

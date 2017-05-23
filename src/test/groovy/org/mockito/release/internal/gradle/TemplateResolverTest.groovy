package org.mockito.release.internal.gradle

import spock.lang.Specification

class TemplateResolverTest extends Specification {

    def input = """
    releasing {
        gitHub.repository = "@gitHub.repository@"
        pkg.licenses = @pkg.licenses@
        buildNo = @buildNo@
    }
"""

    def output = """
    releasing {
        gitHub.repository = "mockito/mockito-release-tools-example"
        pkg.licenses = ['MIT']
        buildNo = System.getenv("TRAVIS_BUILD_NUMBER")
    }
"""

    def "should resolve template" (){
        given:
        def resolver = new TemplateResolver(input)
                            .withProperty("gitHub.repository", "mockito/mockito-release-tools-example")
                            .withProperty("pkg.licenses", "['MIT']")
                            .withProperty("buildNo", "System.getenv(\"TRAVIS_BUILD_NUMBER\")")

        expect:
        resolver.resolve() == output
    }

    def "should fail on null key" (){
        given:
        def resolver = new TemplateResolver("test")

        when:
        resolver.withProperty(null, "value")

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "key cannot be null."
    }

    def "should fail on null value" (){
        given:
        def resolver = new TemplateResolver("test")

        when:
        resolver.withProperty("key", null)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "value cannot be null."
    }
}

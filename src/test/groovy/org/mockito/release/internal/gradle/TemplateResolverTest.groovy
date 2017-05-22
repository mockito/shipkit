package org.mockito.release.internal.gradle

import spock.lang.Specification

class TemplateResolverTest extends Specification {

    def input = """
    releasing {
        gitHub.repository = "@gitHub.repository@"
        gitHub.readOnlyAuthToken = "@gitHub.readOnlyAuthToken@"
    }

    allprojects {
        plugins.withId("org.mockito.mockito-release-tools.bintray") {
            bintray {
                pkg {
                    repo = '@bintray.pkg.repo@'
                    licenses = @bintray.pkg.licenses@
                    labels = @bintray.pkg.labels@
                }
            }
        }
    }
"""

    def output = """
    releasing {
        gitHub.repository = "mockito/mockito-release-tools-example"
        gitHub.readOnlyAuthToken = "e7fe8fcdd6ffed5c38498c4c79b2a68e6f6ed1bb"
    }

    allprojects {
        plugins.withId("org.mockito.mockito-release-tools.bintray") {
            bintray {
                pkg {
                    repo = 'examples'
                    licenses = ['MIT']
                    labels = ['continuous delivery', 'release automation', 'shipkit']
                }
            }
        }
    }
"""

    def "should resolve template" (){
        given:
        def resolver = new TemplateResolver(input)
                            .withProperty("gitHub.repository", "mockito/mockito-release-tools-example")
                            .withProperty("gitHub.readOnlyAuthToken", "e7fe8fcdd6ffed5c38498c4c79b2a68e6f6ed1bb")
                            .withProperty("bintray.pkg.repo", "examples")
                            .withProperty("bintray.pkg.licenses", "['MIT']")
                            .withProperty("bintray.pkg.labels", "['continuous delivery', 'release automation', 'shipkit']")

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

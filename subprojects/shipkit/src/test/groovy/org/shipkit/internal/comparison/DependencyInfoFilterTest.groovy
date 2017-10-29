package org.shipkit.internal.comparison

import spock.lang.Specification

class DependencyInfoFilterTest extends Specification {

    def input = """
{
    "description" : "desc",
    "dependencies" : [
        {
            "version" : "1.2.3",
            "group" : "org.mockito",
            "name" : "mockito-all"
        },
        {
            "version" : "2.4.6",
            "group" : "org.mockito",
            "name" : "mockito-core"
        },
        {
            "version" : "1.2.3",
            "group" : "org.shipkit",
            "name" : "shipkit"
        }
    ]
}
"""

    def expectedOutput = """{
\t"dependencies":[
\t\t{
\t\t\t"group":"org.mockito",
\t\t\t"name":"mockito-all",
\t\t\t"version":"1.2.3"
\t\t},
\t\t{
\t\t\t"group":"org.mockito",
\t\t\t"name":"mockito-core",
\t\t\t"version":"3.0.0"
\t\t},
\t\t{
\t\t\t"group":"org.shipkit",
\t\t\t"name":"shipkit",
\t\t\t"version":"1.2.3"
\t\t}
\t],
\t"description":"desc"
}"""

    def "should return sorted and with new version for submodules"() {
        expect:
        new DependencyInfoFilter("org.mockito", "2.4.6", "3.0.0").filter(input) == expectedOutput
    }

}

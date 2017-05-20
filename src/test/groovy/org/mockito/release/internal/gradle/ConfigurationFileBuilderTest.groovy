package org.mockito.release.internal.gradle

import spock.lang.Specification

class ConfigurationFileBuilderTest extends Specification {

    def "should build config file properly" (){
        expect:
        def content = new ConfigurationFileBuilder("releasing")
                .withProperty("gitHub.repository", "mockito/mockito")
                .withProperty("git.user", "Mockito Release Tools")
                .withProperty("team.developers", new ArrayList())
                .withProperty("team.contributors", Arrays.asList("wwilk:Wojtek Wilk", "mstachniuk:Marcin Stachniuk"))
                .withProperty("releaseNotes.labelMapping", ["noteworthy":"Noteworthy","bugfix":"Bugfixes"])
                .withProperty("releaseNotes.labelMapping2", [:])
                .withProperty("gitHub.writeAuthToken", new ConfigurationFileBuilder.Expression("System.getenv(\"TOKEN\")"))
                .build()

        content == "releasing {\n" +
                "\tgitHub.repository = \"mockito/mockito\"\n" +
                "\tgit.user = \"Mockito Release Tools\"\n" +
                "\tteam.developers = []\n" +
                "\tteam.contributors = [\"wwilk:Wojtek Wilk\",\"mstachniuk:Marcin Stachniuk\"]\n" +
                "\treleaseNotes.labelMapping = [noteworthy:\"Noteworthy\",bugfix:\"Bugfixes\"]\n" +
                "\treleaseNotes.labelMapping2 = [:]\n" +
                "\tgitHub.writeAuthToken = System.getenv(\"TOKEN\")\n" +
                "}\n"
    }
}

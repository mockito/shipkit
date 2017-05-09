package org.mockito.release.internal.gradle.util.pom;

import groovy.util.Node;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.gradle.util.ReleaseConfigurationTeamParser;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.release.internal.gradle.util.ReleaseConfigurationTeamParser.parsePerson;

public class PomXmlCustomizer {

    /**
     * Customizes pom xml based on the provide configuration and settings
     */
    public static void customizePom(Node root, ReleaseConfiguration conf,
                                    String projectName, String projectDescription) {
        //Assumes project has java plugin applied. Pretty safe assumption
        root.appendNode("name", projectName);
        root.appendNode("packaging", "jar");

        String repoLink = "https://github.com/" + conf.getGitHub().getRepository();
        root.appendNode("url", repoLink);
        root.appendNode("description", projectDescription);

        Node license = root.appendNode("licenses").appendNode("license");
        license.appendNode("name", "The MIT License");
        license.appendNode("url", repoLink + "/blob/master/LICENSE");
        license.appendNode("distribution", "repo");

        root.appendNode("scm").appendNode("url", repoLink + ".git");

        Node issues = root.appendNode("issueManagement");
        issues.appendNode("url", repoLink + "/issues");
        issues.appendNode("system", "GitHub issues");

        Node ci = root.appendNode("ciManagement");
        ci.appendNode("url", "https://travis-ci.org/" + conf.getGitHub().getRepository());
        ci.appendNode("system", "TravisCI");

        if (!conf.getTeam().getDevelopers().isEmpty()) {
            Node developers = root.appendNode("developers");
            for (String notation : conf.getTeam().getDevelopers()) {
                ReleaseConfigurationTeamParser.Person person = parsePerson(notation);
                Node d = developers.appendNode("developer");
                d.appendNode("id", person.gitHubUser);
                d.appendNode("name", person.name);
                d.appendNode("roles").appendNode("role", "Core developer");
                d.appendNode("url", "https://github.com/" + person.gitHubUser);
            }
        }

        if (!conf.getTeam().getContributors().isEmpty()) {
            Set<String> devs = new HashSet<String>(conf.getTeam().getDevelopers());
            Node contributors = root.appendNode("contributors");
            for (String notation : conf.getTeam().getContributors()) {
                if (!devs.contains(notation)) {
                    ReleaseConfigurationTeamParser.Person person = parsePerson(notation);
                    Node d = contributors.appendNode("contributor");
                    d.appendNode("name", person.name);
                    d.appendNode("url", "https://github.com/" + person.gitHubUser);
                }
            }
        }
    }
}

package org.mockito.release.internal.gradle.util.pom;

import groovy.util.Node;
import org.gradle.api.GradleException;
import org.mockito.release.gradle.ReleaseConfiguration;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.release.internal.util.ArgumentValidation.notNull;

public class PomXmlCustomizer {

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
                Person person = parse(notation);
                Node d = developers.appendNode("developer");
                d.appendNode("id", person.gitHubUser);
                d.appendNode("name", person.fullName);
                d.appendNode("roles").appendNode("role", "Core developer");
                d.appendNode("url", "https://github.com/" + person.gitHubUser);
            }
        }

        if (!conf.getTeam().getContributors().isEmpty()) {
            Set<String> devs = new HashSet<String>(conf.getTeam().getDevelopers());
            Node contributors = root.appendNode("contributors");
            for (String notation : conf.getTeam().getContributors()) {
                if (!devs.contains(notation)) {
                    Person person = parse(notation);
                    Node d = contributors.appendNode("contributor");
                    d.appendNode("name", person.fullName);
                    d.appendNode("url", "https://github.com/" + person.gitHubUser);
                }
            }
        }
    }

    static class Person {
        private final String gitHubUser;
        private final String fullName;
        Person(String gitHubUser, String fullName) {
            this.gitHubUser = gitHubUser;
            this.fullName = fullName;
        }
    }

    static Person parse(String notation) {
        notNull(notation, "Team member notation cannot be null");
        String[] split = notation.split(":");
        if (split.length != 2) {
            throw new GradleException("Invalid value of team member: '" + notation + "'" +
                    "\nExample of correct notation: 'szczepiq:Szczepan Faber'" +
                    "\nSee Javadoc for ReleaseConfiguration.Team class.");
        }
        return new Person(split[0], split[1]);
    }
}

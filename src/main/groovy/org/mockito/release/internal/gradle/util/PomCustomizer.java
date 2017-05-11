package org.mockito.release.internal.gradle.util;

import groovy.util.Node;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.XmlProvider;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.publish.maven.MavenPublication;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.gradle.util.team.TeamMember;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.release.internal.gradle.util.team.TeamParser.parsePerson;

/**
 * Customizes the pom file. Intended to be used with Gradle's 'maven-publish' plugin.
 */
public class PomCustomizer {

    private static final Logger LOG = Logging.getLogger(PomCustomizer.class);

    /**
     * Customizes the pom. The method requires following properties on root project to function correctly:
     */
    public static void customizePom(final Project project, final ReleaseConfiguration conf, final MavenPublication publication) {
        publication.getPom().withXml(new Action<XmlProvider>() {
            public void execute(XmlProvider xml) {
                String archivesBaseName = (String) project.getProperties().get("archivesBaseName");
                LOG.info("  Customizing pom for publication " + publication.getName() + " in " + project.toString() +
                        "\n   - Module name (project.archivesBaseName): " + archivesBaseName +
                        "\n   - Description (project.description): " + project.getDescription() +
                        "\n   - GitHub repository (project.rootProject.releasing.gitHub.repository): "
                                + conf.getGitHub().getRepository() +
                        "\n   - Developers (project.rootProject.releasing.team.developers): "
                                + StringUtil.join(conf.getTeam().getDevelopers(), ", ") +
                        "\n   - Contributors (project.rootProject.releasing.team.contributors): "
                                + StringUtil.join(conf.getTeam().getContributors(), ", "));
                customizePom(xml.asNode(), conf, archivesBaseName, project.getDescription());
            }
        });
    }

    /**
     * Customizes pom xml based on the provide configuration and settings
     */
    static void customizePom(Node root, ReleaseConfiguration conf,
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
                TeamMember person = parsePerson(notation);
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
                    TeamMember person = parsePerson(notation);
                    Node d = contributors.appendNode("contributor");
                    d.appendNode("name", person.name);
                    d.appendNode("url", "https://github.com/" + person.gitHubUser);
                }
            }
        }
    }
}

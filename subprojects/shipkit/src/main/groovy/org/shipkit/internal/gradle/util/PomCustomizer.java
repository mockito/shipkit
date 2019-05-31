package org.shipkit.internal.gradle.util;

import groovy.util.Node;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.XmlProvider;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.publish.maven.MavenPublication;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.util.team.TeamMember;
import org.shipkit.internal.notes.contributors.ProjectContributorsSerializer;
import org.shipkit.internal.notes.contributors.ProjectContributorsSet;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.shipkit.internal.gradle.util.BuildConventions.contributorsFile;
import static org.shipkit.internal.gradle.util.team.TeamParser.parsePerson;

/**
 * Customizes the pom file. Intended to be used with Gradle's 'maven-publish' plugin.
 */
public class PomCustomizer {

    private static final Logger LOG = Logging.getLogger(PomCustomizer.class);

    /**
     * Customizes the pom. The method requires following properties on root project to function correctly:
     */
    public static void customizePom(final Project project, final ShipkitConfiguration conf, final MavenPublication publication) {
        publication.getPom().withXml(new Action<XmlProvider>() {
            public void execute(XmlProvider xml) {
                String archivesBaseName = (String) project.getProperties().get("archivesBaseName");
                File contributorsFile = contributorsFile(project);
                LOG.info("  Read project contributors from file: " + contributorsFile.getAbsolutePath());

                // It can happens that contributorsFile doesn't exist e.g. when shipkit.team.contributors is NOT empty
                ProjectContributorsSet contributorsFromGitHub = new ProjectContributorsSerializer()
                        .deserialize(IOUtil.readFullyOrDefault(contributorsFile, "[]"));
                LOG.info("  Customizing pom for publication " + publication.getName() + " in " + project.toString() +
                        "\n   - Module name (project.archivesBaseName): " + archivesBaseName +
                        "\n   - Description (project.description): " + project.getDescription() +
                        "\n   - GitHub repository (project.rootProject.shipkit.gitHub.repository): "
                                + conf.getGitHub().getRepository() +
                        "\n   - Developers (project.rootProject.shipkit.team.developers): "
                                + StringUtil.join(conf.getTeam().getDevelopers(), ", ") +
                        "\n   - Contributors (project.rootProject.shipkit.team.contributors): "
                                + StringUtil.join(conf.getTeam().getContributors(), ", ") +
                        "\n   - Contributors read from GitHub: "
                                + StringUtil.join(contributorsFromGitHub.toConfigNotation(), ", "));

                final boolean isAndroidLibrary = project.getPlugins().hasPlugin("com.android.library");
                customizePom(xml.asNode(), conf, archivesBaseName, project.getDescription(), contributorsFromGitHub, isAndroidLibrary);
            }
        });
    }

    /**
     * Customizes pom xml based on the provide configuration and settings
     */
    static void customizePom(Node root, ShipkitConfiguration conf,
                             String projectName, String projectDescription,
                             ProjectContributorsSet contributorsFromGitHub,
                             boolean isAndroidLibrary) {
        root.appendNode("name", projectName);
        if (!isAndroidLibrary) {
            //Android library publication uses aar packaging set by aar-publish-plugin
            root.appendNode("packaging", "jar");
        }

        String repoLink = conf.getGitHub().getUrl() + "/" + conf.getGitHub().getRepository();
        root.appendNode("url", repoLink);
        if (projectDescription != null) {
            root.appendNode("description", projectDescription);
        }

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
                d.appendNode("url", conf.getGitHub().getUrl() + "/" + person.gitHubUser);
            }
        }

        if (!conf.getTeam().getContributors().isEmpty() || contributorsFromGitHub.size() != 0) {
            Set<String> devs = new HashSet<>(conf.getTeam().getDevelopers());
            Node contributors = root.appendNode("contributors");
            Collection<String> allContributors = new ArrayList<>();
            allContributors.addAll(conf.getTeam().getContributors());
            allContributors.addAll(contributorsFromGitHub.toConfigNotation());
            for (String notation : allContributors) {
                if (!devs.contains(notation)) {
                    TeamMember person = parsePerson(notation);
                    Node d = contributors.appendNode("contributor");
                    d.appendNode("name", person.name);
                    d.appendNode("url", conf.getGitHub().getUrl() + "/" + person.gitHubUser);
                }
            }
        }
    }
}

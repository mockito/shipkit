package org.mockito.release.notes.contributors;

import groovy.util.Node;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.mockito.release.notes.model.ProjectContributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContributorsToPom {

    private static final Logger LOG = Logging.getLogger(GitHubAllContributorsFetcher.class);

    /**
     * Add contributors to node.
     * Contributors defined in developers list are ignored to avoid duplication.
     *
     * @param contributorsNode       contributors node in pom.xml where contributors will be add
     * @param contributors           list of contributors in format: login:name_surname
     * @param developers             list of developers in format: login:name_surname
     */
    public static void include(Node contributorsNode,
                               List<String> contributors,
                               List<String> developers) {
        new ContributorsToPom().include(contributorsNode, contributors, developers);
    }

    /**
     * Add contributors to node. Contributors defined in developers list are ignored to avoid duplication.
     *
     * @param contributorsNode       contributors node in pom.xml where contributors will be add
     * @param contributorsSet        set of contributors from GitHub
     * @param contributorsStringList list of contributors in format: login:name_surname
     * @param developersStringList             list of developers in format: login:name_surname
     */
    void include(Node contributorsNode,
                 Set<ProjectContributor> contributorsSet,
                 List<String> contributorsStringList,
                 List<String> developersStringList) {
        List<ProjectContributor> contributors = convert(contributorsStringList);
        List<ProjectContributor> developers = convert(developersStringList);
        for (ProjectContributor contributor : contributors) {
            addNode(contributorsNode, contributor);
        }
        for (ProjectContributor contributor : contributorsSet) {
            if(needAddContributorToNode(contributor, developers)) {
                addNode(contributorsNode, contributor);
            }
        }
    }

    private List<ProjectContributor> convert(List<String> contributors) {
        List<ProjectContributor> result = new ArrayList<ProjectContributor>();
        for (String text : contributors) {
            String[] split = text.split(":");
            if (split.length != 2) {
                String message = String.format("%s%s%s%s%s",
                        "Wrong format of contributor. ",
                        "It needs to contains only one : (colon), but it contains ",
                        split.length,
                        ". Wrong entry: ",
                        text);
                throw new RuntimeException(message);
            }
            result.add(new DefaultProjectContributor(split[1], split[0], "https://github.com/" + split[0], -1));
        }
        return result;
    }

    private void addNode(Node contributorsNode, ProjectContributor projectContributor) {
        Node node = contributorsNode.appendNode("contributor");
        node.appendNode("name", extractName(projectContributor));
        node.appendNode("url", projectContributor.getProfileUrl());
    }

    private String extractName(ProjectContributor projectContributor) {
        return projectContributor.getName().isEmpty() ? projectContributor.getLogin() : projectContributor.getName();
    }

    private boolean needAddContributorToNode(ProjectContributor contributor, List<ProjectContributor> developers) {
        for (ProjectContributor developer : developers) {
            if(developer.getLogin().equals(contributor.getLogin())) {
                LOG.info("Ignore adding " + contributor
                        + " to contributors list in pom.xml because of being on developers list");
                return false;
            }
        }
        return true;
    }
}

package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.ProjectContributor;

import java.util.Set;

public interface ProjectContributorsSet {

    void addContributor(ProjectContributor contributor);

    void addAllContributors(Set<ProjectContributor> projectContributors);

    int size();

    Set<ProjectContributor> getAllContributors();

}

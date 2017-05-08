package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.Contributor;
import org.mockito.release.notes.model.ProjectContributor;

import java.util.Collection;
import java.util.Set;

public interface ProjectContributorsSet {

    void addContributor(ProjectContributor contributor);

    void addAllContributors(Collection<ProjectContributor> projectContributors);

    int size();

    Set<ProjectContributor> getAllContributors();

    Contributor findByName(String name);
}

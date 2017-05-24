package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.ProjectContributor;

import java.util.Collection;
import java.util.Set;

public interface ProjectContributorsSet {

    void addContributor(ProjectContributor contributor);

    void addAllContributors(Collection<ProjectContributor> projectContributors);

    int size();

    Set<ProjectContributor> getAllContributors();

    /**
     * Finds project contributor by name.
     * Returns null if one cannot be found.
     */
    ProjectContributor findByName(String name);

    /**
     * Returns a collection of contributors in notation "GITHUB_USER:FULL_NAME".
     */
    Collection<String> toConfigNotation();
}

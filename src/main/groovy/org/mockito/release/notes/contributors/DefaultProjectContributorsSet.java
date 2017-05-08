package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.Contributor;
import org.mockito.release.notes.model.ProjectContributor;

import java.io.Serializable;
import java.util.*;

class DefaultProjectContributorsSet implements ProjectContributorsSet, Serializable {

    private final Set<ProjectContributor> contributors = new HashSet<ProjectContributor>();
    private final Set<ProjectContributor> sorted = new TreeSet<ProjectContributor>(Collections.<ProjectContributor>reverseOrder());

    @Override
    public void addContributor(ProjectContributor contributorToAdd) {
        if (contributors.add(contributorToAdd)) {
            //avoiding duplicates in the sorted collection, see unit tests
            sorted.add(contributorToAdd);
        }
    }

    @Override
    public void addAllContributors(Collection<ProjectContributor> contributorsToAdd) {
        if (contributors.addAll(contributorsToAdd)) {
            sorted.addAll(contributors);
        }
    }

    @Override
    public int size() {
        return contributors.size();
    }

    @Override
    public Set<ProjectContributor> getAllContributors() {
        return sorted;
    }

    @Override
    public Contributor findByName(String name) {
        return null;
    }
}

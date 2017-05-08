package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.ProjectContributor;

import java.io.Serializable;
import java.util.*;

class DefaultProjectContributorsSet implements ProjectContributorsSet, Serializable {

    private final Set<ProjectContributor> contributors = new HashSet<ProjectContributor>();
    private Set<ProjectContributor> sorted = new TreeSet<ProjectContributor>();

    @Override
    public void addContributor(ProjectContributor contributorToAdd) {
        if(contributors.add(contributorToAdd)) {
            //avoiding duplicates in the sorted collection, see unit tests
            sorted.add(contributorToAdd);
        }
    }

    @Override
    public void addAllContributors(Set<ProjectContributor> contributorsToAdd) {
        if(contributors.addAll(contributorsToAdd)) {
            //avoiding duplicates in the sorted collection, see unit tests
            sorted = new TreeSet<ProjectContributor>(contributorsToAdd);
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

}

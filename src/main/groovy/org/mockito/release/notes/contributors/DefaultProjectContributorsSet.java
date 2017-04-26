package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.ProjectContributor;

import java.io.Serializable;
import java.util.*;

class DefaultProjectContributorsSet implements ProjectContributorsSet, Serializable {

    private final Set<ProjectContributor> contributors;

    DefaultProjectContributorsSet() {
        contributors = new TreeSet<ProjectContributor>(new Comparator<ProjectContributor>() {
            @Override
            public int compare(ProjectContributor o1, ProjectContributor o2) {
                return o2.getNumberOfContributions() - o1.getNumberOfContributions();   // sort descend
            }
        });
    }

    @Override
    public void addContributor(ProjectContributor contributorToAdd) {
        contributors.add(contributorToAdd);
    }

    @Override
    public void addAllContributors(Set<ProjectContributor> contributorsToAdd) {
        contributors.addAll(contributorsToAdd);
    }

    @Override
    public int size() {
        return contributors.size();
    }

    @Override
    public Set<ProjectContributor> getAllContributors() {
        return contributors;
    }

}

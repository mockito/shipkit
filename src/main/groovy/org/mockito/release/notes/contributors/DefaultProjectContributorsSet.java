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
                //TODO this looks hacky.
                // Let's make DefaultProjectContributor an instance of Comparable, similar to how we do it in DefaultContribution.
                if (o1.equals(o2)) {
                    return 0;
                }

                int result = o2.getNumberOfContributions() - o1.getNumberOfContributions(); // descend
                if (result == 0 && !o1.equals(o2)) {
                    return -1; //the result does not matter so long it's not '0' (it would drop the element from collection)
                }
                return result;
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

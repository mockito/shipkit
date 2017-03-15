package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.Contributor;

import java.util.Set;

public interface ContributorsSet {

    Contributor findByAuthorName(String authorName);

    void addContributor(Contributor contributor);

    void addAllContributors(Set<Contributor> contributors);

    int size();
}

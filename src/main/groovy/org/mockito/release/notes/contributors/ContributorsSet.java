package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.Contributor;

import java.util.Collection;
import java.util.Set;

public interface ContributorsSet<T extends Contributor> {

    T findByAuthorName(String authorName);

    void addContributor(T contributor);

    void addAllContributors(Set<T> contributors);

    int size();

    Collection<T> getAllContributors();
}

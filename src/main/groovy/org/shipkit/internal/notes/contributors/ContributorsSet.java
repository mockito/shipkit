package org.shipkit.internal.notes.contributors;

import org.shipkit.internal.notes.model.Contributor;

import java.util.Collection;
import java.util.Set;

public interface ContributorsSet {

    Contributor findByAuthorName(String authorName);

    void addContributor(Contributor contributor);

    void addAllContributors(Set<Contributor> contributors);

    int size();

    Collection<Contributor> getAllContributors();
}

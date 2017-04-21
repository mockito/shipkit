package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.Contributor;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class DefaultContributorsSet<E extends Contributor> implements ContributorsSet<E>, Serializable {

    private final Map<String, E> map;

    DefaultContributorsSet() {
        map = new HashMap<String, E>();
    }

    @Override
    public E findByAuthorName(String authorName) {
        return map.get(authorName);
    }

    @Override
    public void addContributor(E contributor) {
        map.put(contributor.getName(), contributor);
    }

    @Override
    public void addAllContributors(Set<E> contributors) {
        for (E contributor : contributors) {
            map.put(contributor.getName(), contributor);
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Collection<E> getAllContributors() {
        return map.values();
    }

}

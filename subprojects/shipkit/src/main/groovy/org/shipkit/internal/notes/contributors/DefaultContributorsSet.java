package org.shipkit.internal.notes.contributors;

import org.shipkit.internal.notes.model.Contributor;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class DefaultContributorsSet implements ContributorsSet, Serializable {

    private final Map<String, Contributor> map;

    DefaultContributorsSet() {
        map = new HashMap<String, Contributor>();
    }

    @Override
    public Contributor findByAuthorName(String authorName) {
        return map.get(authorName);
    }

    @Override
    public void addContributor(Contributor contributor) {
        map.put(contributor.getName(), contributor);
    }

    @Override
    public void addAllContributors(Set<Contributor> contributors) {
        for (Contributor contributor : contributors) {
            map.put(contributor.getName(), contributor);
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Collection<Contributor> getAllContributors() {
        return map.values();
    }

}

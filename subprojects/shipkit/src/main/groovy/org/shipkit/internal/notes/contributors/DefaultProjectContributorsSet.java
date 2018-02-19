package org.shipkit.internal.notes.contributors;

import org.shipkit.internal.notes.model.Contributor;
import org.shipkit.internal.notes.model.ProjectContributor;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;

public class DefaultProjectContributorsSet implements ProjectContributorsSet, Serializable {

    private final Predicate<Contributor> ignoredContributor;
    //This set is used to manage uniqueness of contributors:
    private final Set<ProjectContributor> contributors = new HashSet<ProjectContributor>();
    //To keep sorted contributors ready to be used:
    private final Set<ProjectContributor> sorted = new TreeSet<ProjectContributor>(Collections.<ProjectContributor>reverseOrder());
    //For fast lookups:
    private final Map<String, ProjectContributor> map = new HashMap<String, ProjectContributor>();

    public DefaultProjectContributorsSet() {
        this.ignoredContributor = IgnoredContributor.none();
    }

    public DefaultProjectContributorsSet(Collection<String> ignoredContributors) {
        this.ignoredContributor = IgnoredContributor.of(ignoredContributors);
    }

    @Override
    public void addContributor(ProjectContributor contributor) {
        if (!ignoredContributor.test(contributor) && contributors.add(contributor)) {
            //avoiding duplicates in the sorted collection, see unit tests
            sorted.add(contributor);
            map.put(contributor.getName(), contributor);
        }
    }

    @Override
    public void addAllContributors(Collection<ProjectContributor> contributors) {
        for (ProjectContributor c : contributors) {
            addContributor(c);
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
    public ProjectContributor findByName(String name) {
        return map.get(name);
    }

    @Override
    public Collection<String> toConfigNotation() {
        List<String> result = new ArrayList<String>();
        for (ProjectContributor contributor : sorted) {
            // if someone doesn't set a name on GitHub, lets put login
            String name = contributor.getName().isEmpty() ? contributor.getLogin() : contributor.getName();
            result.add(contributor.getLogin() + ":" + name);
        }
        return result;
    }

    @Override
    public String toString() {
        return sorted.toString();
    }
}

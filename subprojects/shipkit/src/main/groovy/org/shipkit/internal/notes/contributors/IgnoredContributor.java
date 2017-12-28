package org.shipkit.internal.notes.contributors;

import org.shipkit.internal.notes.model.Contributor;
import org.shipkit.internal.notes.util.Predicate;

import java.util.Collection;
import java.util.Collections;

class IgnoredContributor implements Predicate<Contributor> {

    static IgnoredContributor none(){
        return new IgnoredContributor(Collections.<String>emptyList());
    }

    static IgnoredContributor of(Collection<String> ignoredContributors){
        return new IgnoredContributor(ignoredContributors);
    }

    private final Collection<String> ignoredContributors;

    private IgnoredContributor(Collection<String> ignoredContributors) {
        this.ignoredContributors = ignoredContributors;
    }

    @Override
    public boolean isTrue(Contributor contributor) {
        for(String ignoredContributor : ignoredContributors){
            if(ignoredContributor.equals(contributor.getLogin())){
                return true;
            }
        }

        return false;
    }
}

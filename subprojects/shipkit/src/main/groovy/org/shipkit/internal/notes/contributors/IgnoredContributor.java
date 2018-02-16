package org.shipkit.internal.notes.contributors;

import org.shipkit.internal.notes.model.Contributor;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

public class IgnoredContributor implements Predicate<Contributor> {

    private final Collection<String> ignoredContributors;

    public static IgnoredContributor none() {
        return new IgnoredContributor(Collections.<String>emptyList());
    }

    public static IgnoredContributor of(Collection<String> ignoredContributors) {
        return new IgnoredContributor(ignoredContributors);
    }

    private IgnoredContributor(Collection<String> ignoredContributors) {
        this.ignoredContributors = ignoredContributors;
    }

    @Override
    public boolean test(Contributor contributor) {
        String contributorLogin = contributor.getLogin();
        String contributorName = contributor.getName();

        return test(contributorName) || test(contributorLogin);
    }

    public boolean test(String contributorName) {
        for (String ignoredContributor : ignoredContributors) {
            if (ignoredContributor.equals(contributorName)) {
                return true;
            }
        }

        return false;
    }
}

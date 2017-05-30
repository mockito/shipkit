package org.shipkit.notes.improvements;

import org.shipkit.notes.model.ContributionSet;
import org.shipkit.notes.model.Improvement;

import java.util.Collection;

/**
 * Provides tracked improvements to be referenced in release notes
 */
public interface ImprovementsProvider {

    /**
     * Returns improvements that are referenced from given contribution set.
     *
     * @param contributions refer improvements
     * @param labels get only improvements that have one of supplied labels.
     *               Empty collection is ok and it means that you want all improvements.
     * @param onlyPullRequests if true, only improvements that are pull requests are returned.
     */
    Collection<Improvement> getImprovements(ContributionSet contributions, Collection<String> labels, boolean onlyPullRequests);
}

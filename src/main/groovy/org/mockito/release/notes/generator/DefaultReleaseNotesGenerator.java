package org.mockito.release.notes.generator;

import org.mockito.release.notes.improvements.ImprovementsProvider;
import org.mockito.release.notes.internal.DefaultReleaseNotesData;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.notes.vcs.ContributionsProvider;
import org.mockito.release.notes.vcs.ReleaseDateProvider;

import java.util.*;

class DefaultReleaseNotesGenerator implements ReleaseNotesGenerator {

    private final ContributionsProvider contributionsProvider;
    private final ImprovementsProvider improvementsProvider;
    private final ReleaseDateProvider releaseDateProvider;

    DefaultReleaseNotesGenerator(ContributionsProvider contributionsProvider, ImprovementsProvider improvementsProvider, ReleaseDateProvider releaseDateProvider) {
        this.contributionsProvider = contributionsProvider;
        this.improvementsProvider = improvementsProvider;
        this.releaseDateProvider = releaseDateProvider;
    }

    public Collection<ReleaseNotesData> generateReleaseNotesData(Collection<String> targetVersions, String tagPrefix,
                                                                 Collection<String> gitHubLabels, boolean onlyPullRequests) {
        List<ReleaseNotesData> out = new LinkedList<ReleaseNotesData>();

        Map<String, Date> releaseDates = releaseDateProvider.getReleaseDates(targetVersions, tagPrefix);

        String to = null;
        for (String v : targetVersions) {
            if (to == null) {
                to = v;
                continue;
            }
            String fromRev = tagPrefix + v;
            String toRev = tagPrefix + to;

            ContributionSet contributions = contributionsProvider.getContributionsBetween(fromRev, toRev);
            Collection<Improvement> improvements = improvementsProvider.getImprovements(contributions, gitHubLabels, onlyPullRequests);
            out.add(new DefaultReleaseNotesData(to, releaseDates.get(to), contributions, improvements, fromRev, toRev));

            //next version
            to = v;
        }

        return out;
    }
}

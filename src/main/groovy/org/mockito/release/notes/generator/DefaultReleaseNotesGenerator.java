package org.mockito.release.notes.generator;

import org.mockito.release.notes.contributors.ContributorsSet;
import org.mockito.release.notes.contributors.ContributorsProvider;
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
    private final ContributorsProvider contributorsProvider;

    DefaultReleaseNotesGenerator(ContributionsProvider contributionsProvider, ImprovementsProvider improvementsProvider,
                                 ReleaseDateProvider releaseDateProvider, ContributorsProvider contributorsProvider) {
        this.contributionsProvider = contributionsProvider;
        this.improvementsProvider = improvementsProvider;
        this.releaseDateProvider = releaseDateProvider;
        this.contributorsProvider = contributorsProvider;
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
            ContributorsSet contributors = contributorsProvider.mapContributorsToGitHubUser(contributions, fromRev, toRev);
            out.add(new DefaultReleaseNotesData(to, releaseDates.get(to), contributions, improvements, contributors, fromRev, toRev));

            //next version
            to = v;
        }

        return out;
    }
}

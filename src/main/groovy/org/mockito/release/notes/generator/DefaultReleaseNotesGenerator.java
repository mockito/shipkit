package org.mockito.release.notes.generator;

import org.mockito.release.notes.improvements.ImprovementsProvider;
import org.mockito.release.notes.internal.DefaultReleaseNotesData;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.notes.vcs.ContributionsProvider;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

class DefaultReleaseNotesGenerator implements ReleaseNotesGenerator {

    private final ContributionsProvider contributionsProvider;
    private final ImprovementsProvider improvementsProvider;

    DefaultReleaseNotesGenerator(ContributionsProvider contributionsProvider, ImprovementsProvider improvementsProvider) {
        this.contributionsProvider = contributionsProvider;
        this.improvementsProvider = improvementsProvider;
    }

    public Collection<ReleaseNotesData> generateReleaseNotes(Collection<String> targetVersions, String tagPrefix, Collection<String> gitHubLabels) {
        List<ReleaseNotesData> out = new LinkedList<ReleaseNotesData>();

        //TODO SF use this to get the tag date: 2.6.7
        //git log --tags --simplify-by-decoration --pretty="format:%ai %d"

        String to = null;
        for (String v : targetVersions) {
            if (to == null) {
                to = v;
                continue;
            }
            String fromRev = tagPrefix + v;
            String toRev = tagPrefix + to;

            ContributionSet contributions = contributionsProvider.getContributionsBetween(fromRev, toRev);
            Collection<Improvement> improvements = improvementsProvider.getImprovements(contributions, gitHubLabels);
            out.add(new DefaultReleaseNotesData(to, new Date(), contributions, improvements));

            //next round
            to = v;
        }

        return out;
    }
}

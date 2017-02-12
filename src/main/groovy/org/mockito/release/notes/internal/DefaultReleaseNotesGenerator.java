package org.mockito.release.notes.internal;

import org.mockito.release.notes.DefaultReleaseNotesData;
import org.mockito.release.notes.generator.ReleaseNotesGenerator;
import org.mockito.release.notes.improvements.ImprovementsProvider;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.notes.vcs.ContributionsProvider;

import java.util.*;

public class DefaultReleaseNotesGenerator implements ReleaseNotesGenerator {

    private final ContributionsProvider contributionsProvider;
    private final ImprovementsProvider improvementsProvider;

    public DefaultReleaseNotesGenerator(ContributionsProvider contributionsProvider, ImprovementsProvider improvementsProvider) {
        this.contributionsProvider = contributionsProvider;
        this.improvementsProvider = improvementsProvider;
    }

    public Collection<ReleaseNotesData> generateReleaseNotes(String startVersion, Collection<String> targetVersions, String tagPrefix, Collection<String> gitHubLabels) {
        List<ReleaseNotesData> out = new LinkedList<ReleaseNotesData>();

        //TODO SF use this to get the tag date: 2.6.7
        //git log --tags --simplify-by-decoration --pretty="format:%ai %d"

        String startRev = tagPrefix + startVersion;
        for (String v : targetVersions) {
            String endRev = tagPrefix + v;
            ContributionSet contributions = contributionsProvider.getContributionsBetween(startRev, endRev);
            Collection<Improvement> improvements = improvementsProvider.getImprovements(contributions, gitHubLabels);
            out.add(new DefaultReleaseNotesData(v, new Date(), contributions, improvements));

            //next round
            startRev = endRev;
        }

        return out;
    }
}

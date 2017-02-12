package org.mockito.release.notes.internal;

import org.mockito.release.notes.DefaultVersionNotesData;
import org.mockito.release.notes.generator.ReleaseNotesGenerator;
import org.mockito.release.notes.generator.ReleaseNotesParameters;
import org.mockito.release.notes.improvements.ImprovementsProvider;
import org.mockito.release.notes.model.ContributionSet;
import org.mockito.release.notes.model.Improvement;
import org.mockito.release.notes.model.VersionNotesData;
import org.mockito.release.notes.vcs.ContributionsProvider;

import java.util.*;

public class DefaultReleaseNotesGenerator implements ReleaseNotesGenerator {

    private final ContributionsProvider contributionsProvider;
    private final ImprovementsProvider improvementsProvider;

    public DefaultReleaseNotesGenerator(ContributionsProvider contributionsProvider, ImprovementsProvider improvementsProvider) {
        this.contributionsProvider = contributionsProvider;
        this.improvementsProvider = improvementsProvider;
    }

    public Collection<VersionNotesData> generateReleaseNotes(ReleaseNotesParameters parameters) {
        List<VersionNotesData> out = new LinkedList<VersionNotesData>();

        String startRev = parameters.getTagPrefix() + parameters.getStartVersion();
        for (String v : parameters.getTargetVersions()) {
            String endRev = parameters.getTagPrefix() + v;
            ContributionSet contributions = contributionsProvider.getContributionsBetween(startRev, endRev);
            Collection<Improvement> improvements = improvementsProvider.getImprovements(contributions, parameters.getLabels());
            out.add(new DefaultVersionNotesData(v, new Date(), contributions, improvements));

            //next round
            startRev = endRev;
        }

        return out;
    }
}

package org.mockito.release.notes.generator;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
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

    private final static Logger LOG = Logging.getLogger(DefaultReleaseNotesGenerator.class);

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

        LOG.lifecycle("Generating release notes data for:" +
            "\n  - target versions: " + targetVersions +
            "\n  - GitHub labels: " + gitHubLabels +
            "\n  - only pull requests: " + onlyPullRequests +
            "\n  - version tag prefix: '" + tagPrefix + "'"
        );

        Map<String, Date> releaseDates = releaseDateProvider.getReleaseDates(targetVersions, tagPrefix);
        LOG.lifecycle("Retrieved " + releaseDates.size() + " release date(s).");

        String to = null;
        for (String v : targetVersions) {
            if (to == null) {
                to = v;
                continue;
            }
            String fromRev = tagPrefix + v;
            String toRev = tagPrefix + to;

            ContributionSet contributions = contributionsProvider.getContributionsBetween(fromRev, toRev);
            LOG.lifecycle("Retrieved " + contributions.getContributions().size() + " contribution(s) between " + fromRev + ".." + toRev);

            Collection<Improvement> improvements = improvementsProvider.getImprovements(contributions, gitHubLabels, onlyPullRequests);
            LOG.lifecycle("Retrieved " + improvements.size() + " improvement(s) for tickets: " + contributions.getAllTickets());

            //TODO below is duplicated if the author is the same
            LOG.lifecycle("Getting contributor details for " + contributions.getAuthorCount() + " author(s).");
            ContributorsSet contributors = contributorsProvider.mapContributorsToGitHubUser(contributions, fromRev, toRev);

            out.add(new DefaultReleaseNotesData(to, releaseDates.get(to), contributions, improvements, contributors, fromRev, toRev));

            //next version
            to = v;
        }

        return out;
    }
}

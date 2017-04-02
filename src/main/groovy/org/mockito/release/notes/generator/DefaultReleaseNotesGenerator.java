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
import org.mockito.release.notes.vcs.ReleasedVersion;
import org.mockito.release.notes.vcs.ReleasedVersionsProvider;

import java.util.*;

class DefaultReleaseNotesGenerator implements ReleaseNotesGenerator {

    private final static Logger LOG = Logging.getLogger(DefaultReleaseNotesGenerator.class);

    private final ContributionsProvider contributionsProvider;
    private final ImprovementsProvider improvementsProvider;
    private final ReleasedVersionsProvider releasedVersionsProvider;
    private final ContributorsProvider contributorsProvider;

    DefaultReleaseNotesGenerator(ContributionsProvider contributionsProvider, ImprovementsProvider improvementsProvider,
                                 ReleasedVersionsProvider releasedVersionsProvider, ContributorsProvider contributorsProvider) {
        this.contributionsProvider = contributionsProvider;
        this.improvementsProvider = improvementsProvider;
        this.releasedVersionsProvider = releasedVersionsProvider;
        this.contributorsProvider = contributorsProvider;
    }

    public Collection<ReleaseNotesData> generateReleaseNotesData(String headVersion, Collection<String> targetVersions, String tagPrefix,
                                                                 Collection<String> gitHubLabels, boolean onlyPullRequests) {
        List<ReleaseNotesData> out = new LinkedList<ReleaseNotesData>();

        LOG.lifecycle("Generating release notes data for:" +
            "\n  - target versions: " + targetVersions +
            "\n  - GitHub labels: " + gitHubLabels +
            "\n  - only pull requests: " + onlyPullRequests +
            "\n  - version tag prefix: '" + tagPrefix + "'"
        );

        Collection<ReleasedVersion> versions = releasedVersionsProvider.getReleasedVersions(headVersion, new Date(), targetVersions, tagPrefix);

        for (ReleasedVersion v : versions) {
            if (v.getPreviousRev() == null) {
                continue;
            }
            ContributionSet contributions = contributionsProvider.getContributionsBetween(v.getPreviousRev(), v.getRev());
            LOG.lifecycle("Retrieved " + contributions.getContributions().size() + " contribution(s) between " + v.getPreviousRev() + ".." + v.getRev());

            Collection<Improvement> improvements = improvementsProvider.getImprovements(contributions, gitHubLabels, onlyPullRequests);
            LOG.lifecycle("Retrieved " + improvements.size() + " improvement(s) for tickets: " + contributions.getAllTickets());

            //TODO below is duplicated if the author is the same and he is already mapped
            LOG.lifecycle("Getting contributor details for " + contributions.getAuthorCount() + " author(s).");
            ContributorsSet contributors = contributorsProvider.mapContributorsToGitHubUser(contributions, v.getPreviousRev(), v.getRev());

            out.add(new DefaultReleaseNotesData(v.getVersion(), v.getDate(), contributions, improvements, contributors, v.getPreviousRev(), v.getRev()));
        }

        return out;
    }
}

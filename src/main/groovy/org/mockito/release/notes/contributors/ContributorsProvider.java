package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.ContributionSet;

public interface ContributorsProvider {

    ContributorsMap mapContributorsToGitHubUser(ContributionSet contributions, String fromRevision, String toRevision);
}

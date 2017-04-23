package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.ContributionSet;

public interface ContributorsProvider {

    ContributorsSet mapContributorsToGitHubUser(ContributionSet contributions, String fromRevision, String toRevision);

    ProjectContributorsSet getAllContributorsForProject();
}

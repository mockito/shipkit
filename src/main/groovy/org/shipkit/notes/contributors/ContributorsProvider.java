package org.shipkit.notes.contributors;

import java.util.Collection;

public interface ContributorsProvider {

    ContributorsSet mapContributorsToGitHubUser(Collection<String> authorNames, String fromRevision, String toRevision);

    ProjectContributorsSet getAllContributorsForProject();
}

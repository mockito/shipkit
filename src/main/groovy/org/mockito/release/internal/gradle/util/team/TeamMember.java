package org.mockito.release.internal.gradle.util.team;

import org.mockito.release.gradle.ReleaseConfiguration;

/**
 * Represents team member configurable via {@link ReleaseConfiguration.Team#getDevelopers()}
 * and {@link ReleaseConfiguration.Team#getContributors()}
 */
public class TeamMember {
    public final String gitHubUser;
    public final String name;
    TeamMember(String gitHubUser, String name) {
        this.gitHubUser = gitHubUser;
        this.name = name;
    }
}

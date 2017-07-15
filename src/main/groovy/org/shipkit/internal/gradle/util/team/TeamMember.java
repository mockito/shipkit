package org.shipkit.internal.gradle.util.team;

import org.shipkit.gradle.ReleaseConfiguration;

/**
 * Represents team member configurable via {@link org.shipkit.gradle.ReleaseConfiguration.Team#getDevelopers()}
 * and {@link org.shipkit.gradle.ReleaseConfiguration.Team#getContributors()}
 */
public class TeamMember {
    public final String gitHubUser;
    public final String name;
    TeamMember(String gitHubUser, String name) {
        this.gitHubUser = gitHubUser;
        this.name = name;
    }
}

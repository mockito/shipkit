package org.shipkit.internal.gradle.util.team;

/**
 * Represents team member configurable via {@link org.shipkit.gradle.configuration.ShipkitConfiguration.Team#getDevelopers()}
 * and {@link org.shipkit.gradle.configuration.ShipkitConfiguration.Team#getContributors()}
 */
public class TeamMember {
    public final String gitHubUser;
    public final String name;
    TeamMember(String gitHubUser, String name) {
        this.gitHubUser = gitHubUser;
        this.name = name;
    }
}

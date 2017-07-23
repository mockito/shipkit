package org.shipkit.internal.gradle.util.team;

import org.shipkit.gradle.ShipkitConfiguration;

/**
 * Represents team member configurable via {@link ShipkitConfiguration.Team#getDevelopers()}
 * and {@link ShipkitConfiguration.Team#getContributors()}
 */
public class TeamMember {
    public final String gitHubUser;
    public final String name;
    TeamMember(String gitHubUser, String name) {
        this.gitHubUser = gitHubUser;
        this.name = name;
    }
}

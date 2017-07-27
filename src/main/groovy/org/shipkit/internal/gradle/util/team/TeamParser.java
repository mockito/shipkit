package org.shipkit.internal.gradle.util.team;

import org.gradle.api.GradleException;
import org.shipkit.gradle.ShipkitConfiguration;

import java.util.Collection;

import static org.shipkit.internal.util.ArgumentValidation.notNull;

/**
 * Parses team members configurable via {@link ShipkitConfiguration.Team#getDevelopers()}
 * and {@link ShipkitConfiguration.Team#getContributors()}
 */
public class TeamParser {

    /**
     * Validates team memberes configured via {@link ShipkitConfiguration.Team#getDevelopers()}
     * and {@link ShipkitConfiguration.Team#getContributors()}
     */
    public static void validateTeamMembers(Collection<String> teamMembers) throws InvalidInput {
        for (String member : teamMembers) {
            parsePerson(member);
        }
    }

    /**
     * Thrown when the team members are not configured correctly in
     * {@link ShipkitConfiguration.Team#getDevelopers()}
     * or {@link ShipkitConfiguration.Team#getContributors()}
     */
    public static class InvalidInput extends GradleException {
        InvalidInput(String message) {
            super(message);
        }
    }

    /**
     * Parses single person notation provided via {@link ShipkitConfiguration.Team#getDevelopers()}
     * and {@link ShipkitConfiguration.Team#getContributors()}
     */
    public static TeamMember parsePerson(String notation) throws InvalidInput {
        notNull(notation, "Team member notation cannot be null");
        String[] split = notation.split(":");
        if (split.length != 2) {
            throw invalidInput(notation);
        }
        TeamMember person = new TeamMember(split[0], split[1]);
        if (person.gitHubUser.trim().isEmpty() || person.name.trim().isEmpty()) {
            throw invalidInput(notation);
        }
        return person;
    }

    private static InvalidInput invalidInput(String notation) {
        return new InvalidInput("Invalid value of team member: '" + notation + "'" +
                "\nIt should be: 'GITHUB_USER:FULL_NAME'" +
                "\nExample of correct notation: 'szczepiq:Szczepan Faber'" +
                "\nSee Javadoc for ShipkitConfiguration.Team class.");
    }
}

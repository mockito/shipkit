package org.mockito.release.internal.gradle.util;

import org.gradle.api.GradleException;

import java.util.Collection;

import static org.mockito.release.internal.util.ArgumentValidation.notNull;

public class ReleaseConfigurationTeamParser {

    public static void validateTeamMembers(Collection<String> teamMembers) {
        for (String member : teamMembers) {
            parsePerson(member);
        }
    }

    public static class Person {
        public final String gitHubUser;
        public final String name;
        Person(String gitHubUser, String name) {
            this.gitHubUser = gitHubUser;
            this.name = name;
        }
    }

    public static class InvalidInput extends GradleException {
        InvalidInput(String message) {
            super(message);
        }
    }

    public static Person parsePerson(String notation) {
        notNull(notation, "Team member notation cannot be null");
        String[] split = notation.split(":");
        if (split.length != 2) {
            throw invalidInput(notation);
        }
        Person person = new Person(split[0], split[1]);
        if (person.gitHubUser.trim().isEmpty() || person.name.trim().isEmpty()) {
            throw invalidInput(notation);
        }
        return person;
    }

    private static InvalidInput invalidInput(String notation) {
        return new InvalidInput("Invalid value of team member: '" + notation + "'" +
                "\nIt should be: 'GITHUB_USER:FULL_NAME'" +
                "\nExample of correct notation: 'szczepiq:Szczepan Faber'" +
                "\nSee Javadoc for ReleaseConfiguration.Team class.");
    }
}

package org.mockito.release.notes.contributors;

import org.mockito.release.notes.model.Contributor;

public class DefaultContributor implements Contributor {

    private final String name;
    private final String login;
    private final String profileUrl;

    DefaultContributor(String name, String login, String profileUrl) {
        this.name = name;
        this.login = login;
        this.profileUrl = profileUrl;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getProfileUrl() {
        return profileUrl;
    }
}

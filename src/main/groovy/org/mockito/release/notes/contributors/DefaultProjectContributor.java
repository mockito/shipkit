package org.mockito.release.notes.contributors;

import org.json.simple.Jsoner;
import org.mockito.release.notes.model.Contributor;
import org.mockito.release.notes.model.ProjectContributor;

import java.io.IOException;
import java.io.Writer;

public class DefaultProjectContributor implements ProjectContributor {

    private static final String JSON_FORMAT = "{ \"name\": \"%s\", \"login\": \"%s\", \"profileUrl\": \"%s\", " +
            "\"numberOfContributions\": \"%s\" }";

    private final String name;
    private final String login;
    private final String profileUrl;
    private final Integer numberOfContributions;

    public DefaultProjectContributor(String name, String login, String profileUrl, Integer numberOfContributions) {
        this.name = name;
        this.login = login;
        this.profileUrl = profileUrl;
        this.numberOfContributions = numberOfContributions;
    }

    /**
     * Creating project contributor from vanilla contributor,
     * will assume that there is only single contribution.
     */
    public DefaultProjectContributor(Contributor contributor) {
        this(contributor.getName(), contributor.getLogin(), contributor.getProfileUrl(), 1);
    }

    @Override
    public int getNumberOfContributions() {
        return numberOfContributions;
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

    @Override
    public String toJson() {
        return String.format(JSON_FORMAT,
                escapeOrEmpty(name),
                escape(login),
                escape(profileUrl),
                numberOfContributions);
    }

    private String escapeOrEmpty(String name) {
        return name != null ? escape(name) : "";
    }

    private String escape(String login) {
        return Jsoner.escape(login);
    }

    @Override
    public void toJson(Writer writable) throws IOException {
        writable.append(toJson());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultProjectContributor that = (DefaultProjectContributor) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (!login.equals(that.login)) {
            return false;
        }
        return profileUrl.equals(that.profileUrl);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + login.hashCode();
        result = 31 * result + profileUrl.hashCode();
        return result;
    }
}

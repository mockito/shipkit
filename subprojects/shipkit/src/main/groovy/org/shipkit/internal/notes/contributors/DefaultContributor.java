package org.shipkit.internal.notes.contributors;

import org.json.simple.Jsoner;
import org.shipkit.internal.notes.model.Contributor;

import java.io.IOException;
import java.io.Writer;

public class DefaultContributor implements Contributor {

    private static final String JSON_FORMAT = "{ \"name\": \"%s\", \"login\": \"%s\", \"profileUrl\": \"%s\" }";

    private final String name;
    private final String login;
    private final String profileUrl;

    public DefaultContributor(String name, String login, String profileUrl) {
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

    @Override
    public String toJson() {
        return String.format(JSON_FORMAT, Jsoner.escape(name), Jsoner.escape(login), Jsoner.escape(profileUrl));
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

        DefaultContributor that = (DefaultContributor) o;

        if (!name.equals(that.name)) {
            return false;
        }
        if (!login.equals(that.login)) {
            return false;
        }
        return profileUrl.equals(that.profileUrl);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + login.hashCode();
        result = 31 * result + profileUrl.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name + '/' + login;
    }
}

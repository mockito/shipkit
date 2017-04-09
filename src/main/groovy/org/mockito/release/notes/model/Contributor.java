package org.mockito.release.notes.model;

import org.json.simple.Jsonable;
import java.io.Serializable;

public interface Contributor extends Jsonable, Serializable {

    /**
     * The name of the author. For GitHub it would be name e.g. Monalisa Octocat
     */
    String getName();

    /**
     * The login of the author. For GitHub it would be login e.g. octocat
     */
    String getLogin();

    /**
     * The URL to author page. For GitHub it would be GitHub Profile page e.g. https://github.com/octocat
     */
    String getProfileUrl();
}

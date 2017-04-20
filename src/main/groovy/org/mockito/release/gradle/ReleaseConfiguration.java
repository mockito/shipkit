package org.mockito.release.gradle;

import org.gradle.api.GradleException;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO javadoc
 */
public class ReleaseConfiguration {

    private final Map<String, String> configuration = new HashMap<String, String>();

    private final GitHub gitHub = new GitHub();
    private final ReleaseNotes releaseNotes = new ReleaseNotes();
    private final Git git = new Git();
    private final Library library = new Library();
    private final Bintray bintray = new Bintray();

    private boolean dryRun;

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public GitHub getGitHub() {
        return gitHub;
    }

    public ReleaseNotes getReleaseNotes() {
        return releaseNotes;
    }

    public Git getGit() {
        return git;
    }

    public Library getLibrary() {
        return library;
    }

    public Bintray getBintray() {
        return bintray;
    }

    public class GitHub {

        public String getRepository() {
            return getValue("gitHub.repository");
        }

        public void setRepository(String repository) {
            configuration.put("gitHub.repository", repository);
        }

        public String getUser() {
            return null;
        }

        public void setUser(String user) {
        }

        public String getReadOnlyAuthToken() {
            return null;
        }

        public void setReadOnlyAuthToken(String readOnlyAuthToken) {

        }

        public String getWriteAuthToken() {
            return getValue("gitHub.writeAuthToken");
        }

        public void setWriteAuthToken(String writeAuthToken) {
            configuration.put("gitHub.writeAuthToken", writeAuthToken);
        }
    }

    public static class ReleaseNotes {
        public File getFile() {
            return null;
        }

        public void setFile(File file) {

        }

        public File getNotableFile() {
            return null;
        }

        public void setNotableFile(File notableFile) {

        }

        public Map<String, String> getLabelMapping() {
            return null;
        }

        public void setLabelMapping(Map<String, String> labelMapping) {

        }
    }

    public class Git {

        public String getUser() {
            return null;
        }

        public void setUser(String user) {

        }

        public String getEmail() {
            return null;
        }

        public void setEmail(String email) {

        }

        public String getReleasableBranchRegex() {
            return null;
        }

        public void setReleasableBranchRegex(String releasableBranchRegex) {

        }

        /**
         * See {@link #getBranch()}
         */
        public void setBranch(String branch) {
            configuration.put("git.branch", branch);
        }

        /**
         * Returns the branch the release process works on and commits code to.
         * On Travis CI, we configure it to 'TRAVIS_BRANCH' env variable.
         */
        public String getBranch() {
            return configuration.get("git.branch");
        }
    }

    public static class Library {
        public Collection<String> getDevelopers() {
            return null;
        }

        public void setDevelopers(Collection<String> developers) {

        }

        public Collection<String> getContributors() {
            return null;
        }

        public void setContributors(Collection<String> contributors) {

        }
    }

    public class Bintray {
        public String getApiKey() {
            return getValue("bintray.apiKey");
        }

        public void setApiKey(String apiKey) {
            configuration.put("bintray.apiKey", apiKey);
        }
    }

    private String getValue(String key) {
        String value = configuration.get(key);
        if (value == null) {
            throw new GradleException("Please configure 'releasing." + key + "' value.");
        }
        return value;
    }
}
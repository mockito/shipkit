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

    public class GitHub {

        /**
         * GitHub repository name, for example: "mockito/mockito"
         */
        public String getRepository() {
            return getValue("gitHub.repository");
        }

        /**
         * See {@link #getRepository()}
         *
         * @param repository name of the repo, including user or organization section, for example: "mockito/mockito"
         */
        public void setRepository(String repository) {
            configuration.put("gitHub.repository", repository);
        }

        /**
         * GitHub user associated with the write auth token.
         * Needed for the release process to push changes.
         */
        public String getWriteAuthUser() {
            return configuration.get("gitHub.writeAuthUser");
        }

        /**
         * See {@link #getWriteAuthUser()}
         */
        public void setWriteAuthUser(String user) {
            configuration.put("gitHub.writeAuthUser", user);
        }

        /**
         * GitHub read only auth token.
         * Since the token is read-only it is ok to check that in to VCS.
         */
        public String getReadOnlyAuthToken() {
            return getValue("gitHub.readOnlyAuthToken");
        }

        /**
         * See {@link #getReadOnlyAuthToken()}
         */
        public void setReadOnlyAuthToken(String token) {
            configuration.put("gitHub.readOnlyAuthToken", token);
        }

        public String getWriteAuthToken() {
            return getSensitiveValue("gitHub.writeAuthToken");
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

        /**
         * Git user to be used for automated commits made by release automation
         * (version bumps, release notes commits, etc.).
         * For example: "mockito.release.tools"
         */
        public String getUser() {
            return configuration.get("git.user");
        }

        /**
         * See {@link #getUser()} ()}
         */
        public void setUser(String user) {
            configuration.put("git.user", user);
        }

        /**
         * Git email to be used for automated commits made by release automation
         * (version bumps, release notes commits, etc.).
         * For example "mockito.release.tools@gmail.com"
         */
        public String getEmail() {
            return configuration.get("git.email");
        }

        /**
         * See {@link #getEmail()}
         */
        public void setEmail(String email) {
            configuration.put("git.email", email);
        }

        /**
         * Regex to be used to identify branches that entitled to be released, for example "master|release/.+"
         */
        public String getReleasableBranchRegex() {
            return configuration.get("git.releasableBranchRegex");
        }

        /**
         * See {@link #getReleasableBranchRegex()}
         */
        public void setReleasableBranchRegex(String releasableBranchRegex) {
            configuration.put("git.releasableBranchRegex", releasableBranchRegex);
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

    //TODO unit test message creation and error handling
    private String getValue(String key) {
        return getValue(key, "Please configure 'releasing." + key + "' value.");
    }

    private String getSensitiveValue(String key) {
        return getValue(key, "Please configure 'releasing." + key + "' value.\n" +
                "  It is recommended to use env variable for sensitive information\n" +
                "  and store secured value with your CI configuration.\n" +
                "  Example 'build.gradle' file:\n" +
                "    releasing." + key + " = System.getenv('SECRET')");
    }

    private String getValue(String key, String message) {
        String value = configuration.get(key);
        if (value == null) {
            throw new GradleException(message);
        }
        return value;
    }
}
package org.mockito.release.gradle;

import org.gradle.api.GradleException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration of the releasing plugin.
 * <p>
 * Example of a release configuration of a working example project
 * <a href="https://github.com/mockito/mockito-release-tools-example/blob/master/gradle/release.gradle">on GitHub</a>.
 * <p>
 * For minimal and full configuration, see the
 * <a href="https://github.com/mockito/mockito-release-tools/issues/76">issue 76</a>
 */
public class ReleaseConfiguration {

    private final Map<String, Object> configuration = new HashMap<String, Object>();

    private final GitHub gitHub = new GitHub();
    private final ReleaseNotes releaseNotes = new ReleaseNotes();
    private final Git git = new Git();
    private final Team team = new Team();

    public ReleaseConfiguration() {
        //Configure default values
        this.git.setTagPrefix("v");
    }

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

    public Team getTeam() {
        return team;
    }

    public class GitHub {

        /**
         * GitHub repository name, for example: "mockito/mockito"
         */
        public String getRepository() {
            return getString("gitHub.repository");
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
            return getString("gitHub.writeAuthUser");
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
            return getString("gitHub.readOnlyAuthToken");
        }

        /**
         * See {@link #getReadOnlyAuthToken()}
         */
        public void setReadOnlyAuthToken(String token) {
            configuration.put("gitHub.readOnlyAuthToken", token);
        }

        public String getWriteAuthToken() {
            return getSensitiveString("gitHub.writeAuthToken");
        }

        public void setWriteAuthToken(String writeAuthToken) {
            configuration.put("gitHub.writeAuthToken", writeAuthToken);
        }
    }

    public class ReleaseNotes {

        /**
         * Release notes file relative path, for example: "docs/release-notes.md"
         */
        public String getFile() {
            return getString("releaseNotes.file");
        }

        /**
         * See {@link #getFile()}
         */
        public void setFile(String file) {
            configuration.put("releaseNotes.file", file);
        }

        /**
         * Notable release notes file, for example "docs/notable-release-notes.md"
         */
        public String getNotableFile() {
            return getString("releaseNotes.notableFile");
        }

        /**
         * See {@link #getNotableFile()}
         */
        public void setNotableFile(String notableFile) {
            configuration.put("releaseNotes.notableFile", notableFile);
        }

        /**
         * Issue tracker label mappings.
         * The mapping of issue tracker labels (for example "GitHub label") to human readable and presentable name.
         * The order of labels is important and will influence the order
         * in which groups of issues are generated in release notes.
         * Examples: ['java-9': 'Java 9 support', 'BDD': 'Behavior-Driven Development support']
         */
        public Map<String, String> getLabelMapping() {
            return getMap("releaseNotes.labelMapping");
        }

        /**
         * See {@link #getLabelMapping()}
         */
        public void setLabelMapping(Map<String, String> labelMapping) {
            configuration.put("releaseNotes.labelMapping", labelMapping);
        }
    }

    public class Git {

        /**
         * Git user to be used for automated commits made by release automation
         * (version bumps, release notes commits, etc.).
         * For example: "mockito.release.tools"
         */
        public String getUser() {
            return getString("git.user");
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
            return getString("git.email");
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
            return getString("git.releasableBranchRegex");
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
            return getString("git.branch");
        }

        /**
         * Prefix added to the version to create VCS-addressable tag,
         * for example: "v".
         * Empty string is ok and it means that there is not prefix.
         */
        public String getTagPrefix() {
            return getString("git.tagPrefix");
        }

        /**
         * See {@link #getTagPrefix()}
         */
        public void setTagPrefix(String tagPrefix) {
            configuration.put("git.tagPrefix", tagPrefix);
        }
    }

    /**
     * Team configuration
     */
    public class Team {

        /**
         * Developers to include in generated pom file.
         * It should be a collection of elements like "GITHUB_USER:FULL_NAME", example:
         * ['szczepiq:Szczepan Faber', 'mstachniuk:Marcin Stachniuk'].
         * <p>
         * See POM reference for <a href="https://maven.apache.org/pom.html#Developers">Developers</a>.
         */
        public Collection<String> getDevelopers() {
            return getCollection("team.developers");
        }

        /**
         * See {@link #getDevelopers()}
         */
        public void setDevelopers(Collection<String> developers) {
            configuration.put("team.developers", developers);
        }

        /**
         * Contributors to include in generated pom file.
         * It should be a collection of elements like "GITHUB_USER:FULL_NAME", example:
         * ['szczepiq:Szczepan Faber', 'mstachniuk:Marcin Stachniuk']
         * <p>
         * See POM reference for <a href="https://maven.apache.org/pom.html#Contributors">Contributors</a>.
         */
        public Collection<String> getContributors() {
            return getCollection("team.contributors");
        }

        /**
         * See {@link #getContributors()}
         */
        public void setContributors(Collection<String> contributors) {
            configuration.put("team.contributors", contributors);
        }
    }

    //TODO unit test message creation and error handling
    private String getString(String key) {
        return (String) getValue(key, "Please configure 'releasing." + key + "' value (String).");
    }

    private Map getMap(String key) {
        return (Map) getValue(key, "Please configure 'releasing." + key + "' value (Map).");
    }

    private Collection<String> getCollection(String key) {
        return (Collection) getValue(key, "Please configure 'releasing." + key + "' value (Collection).");
    }

    private String getSensitiveString(String key) {
        return (String) getValue(key, "Please configure 'releasing." + key + "' value.\n" +
                "  It is recommended to use env variable for sensitive information\n" +
                "  and store secured value with your CI configuration.\n" +
                "  Example 'build.gradle' file:\n" +
                "    releasing." + key + " = System.getenv('SECRET')");
    }

    private Object getValue(String key, String message) {
        Object value = configuration.get(key);
        if (value == null) {
            throw new GradleException(message);
        }
        return value;
    }
}
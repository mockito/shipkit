package org.shipkit.gradle;

import org.gradle.api.GradleException;
import org.shipkit.version.VersionInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.util.team.TeamParser.validateTeamMembers;

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

    private static final String NO_ENV_VARIABLE = null;

    private final Map<String, Object> configuration = new HashMap<String, Object>();

    private final GitHub gitHub = new GitHub();
    private final ReleaseNotes releaseNotes = new ReleaseNotes();
    private final Git git = new Git();
    private final Team team = new Team();

    private String previousReleaseVersion;

    public ReleaseConfiguration() {
        //Configure default values
        git.setTagPrefix("v"); //so that tags are "v1.0", "v2.3.4"
        git.setReleasableBranchRegex("master|release/.+");  // matches 'master', 'release/2.x', 'release/3.x', etc.
        git.setCommitMessagePostfix("[ci skip]");
        git.setUser("Shipkit");
        git.setEmail("<shipkit@gmail.com>");

        releaseNotes.setFile("docs/release-notes.md");
        releaseNotes.setIgnoreCommitsContaining(asList("[ci skip]"));
        releaseNotes.setLabelMapping(Collections.<String, String>emptyMap());

        team.setContributors(Collections.<String>emptyList());
        team.setDevelopers(Collections.<String>emptyList());
    }

    //TODO currently it's not clear when to use class fields and when to use the 'configuration' map
    //Let's make it clear in the docs
    private boolean dryRun = true;

    /**
     * See {@link #isDryRun()}
     */
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * If the release steps should be invoked in "dry run" mode.
     * Relevant only to some kinds of release steps,
     * such as bintray upload, git push.
     */
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

    /**
     * See {@link #getPreviousReleaseVersion()}
     */
    public void setPreviousReleaseVersion(String previousReleaseVersion) {
        this.previousReleaseVersion = previousReleaseVersion;
    }

    /**
     * Return last previously released version number
     * See {@link VersionInfo#getPreviousVersion()}
     */
    public String getPreviousReleaseVersion() {
        return previousReleaseVersion;
    }

    public class GitHub {

        /**
         * GitHub repository name, for example: "mockito/shipkit"
         */
        public String getRepository() {
            return getString("gitHub.repository");
        }

        /**
         * See {@link #getRepository()}
         *
         * @param repository name of the repo, including user or organization section, for example: "mockito/shipkit"
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

        /**
         * GitHub write auth token to be used for pushing code to GitHub.
         * Auth token is used with the user specified in {@link #getWriteAuthUser()}.
         * <strong>WARNING:</strong> please don't commit the write auth token to VCS.
         * Instead export "GH_WRITE_TOKEN" environment variable.
         * The env variable value will be automatically returned by this method.
         */
        public String getWriteAuthToken() {
            return (String) getValue("gitHub.writeAuthToken", "GH_WRITE_TOKEN",
                    "Please export 'GH_WRITE_TOKEN' env variable first!\n" +
                    "  The value of that variable is automatically used for 'shipkit.gitHub.writeAuthToken' setting.\n" +
                    "  It is highly recommended to keep write token secure and store env variable with your CI configuration.\n" +
                    "  Alternatively, you can configure the write token explicitly in the *.gradle file:\n" +
                    "    shipkit.gitHub.writeAuthToken = 'secret'");
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

        /**
         * Release notes are generated based on information in commit messages.
         * If a commit message contains any of texts from this collection,
         * that commit will be ignored and not used for generating release notes.
         */
        public Collection<String> getIgnoreCommitsContaining() {
            return getCollection("releaseNotes.ignoreCommitsContaining");
        }

        /**
         * See {@link #getIgnoreCommitsContaining()}
         */
        public void setIgnoreCommitsContaining(Collection<String> commitMessageParts) {
            configuration.put("releaseNotes.ignoreCommitsContaining", commitMessageParts);
        }
    }

    public class Git {

        /**
         * Git user to be used for automated commits made by release automation
         * (version bumps, release notes commits, etc.).
         * For example: "shipkit"
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
         * For example "shipkit@gmail.com"
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
         * Regex to be used to identify branches that are entitled to be released, for example "master|release/.+"
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

        /**
         * Text which will be included in the commit message for all commits automatically created by the release
         * automation.
         * By default it is configured to append "[ci skip]" keyword which will prevent CI builds on Travis CI.
         */
        public String getCommitMessagePostfix() {
            return getString("git.commitMessagePostfix");
        }

        /**
         * See {@link #getCommitMessagePostfix()}
         */
        public void setCommitMessagePostfix(String commitMessagePostfix) {
            //TODO protect this setter and other relevant from invalid input (null value)
            configuration.put("git.commitMessagePostfix", commitMessagePostfix);
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
            validateTeamMembers(developers);
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
            validateTeamMembers(contributors);
            configuration.put("team.contributors", contributors);
        }
    }

    //TODO unit test message creation and error handling, suggested plan:
    //1. Create wrapper type over 'configuration' map
    //2. Move handling to this new object and make it testable, along with env variables
    private String getString(String key) {
        return getString(key, NO_ENV_VARIABLE);
    }

    private Boolean getBoolean(String key) {
        Object value = configuration.get(key);
        return Boolean.parseBoolean(value.toString());
    }

    private String getString(String key, String envVarName) {
        return (String) getValue(key, envVarName, "Please configure 'shipkit." + key + "' value (String).");
    }

    private Map getMap(String key) {
        return (Map) getValue(key, NO_ENV_VARIABLE, "Please configure 'shipkit." + key + "' value (Map).");
    }

    private Collection<String> getCollection(String key) {
        return (Collection) getValue(key, NO_ENV_VARIABLE, "Please configure 'shipkit." + key + "' value (Collection).");
    }

    private Object getValue(String key, String envVarName, String message) {
        Object value = configuration.get(key);
        if (value != null) {
            return value;
        }

        if (envVarName != null) {
            value = System.getenv(envVarName);
            if (value != NO_ENV_VARIABLE) {
                return value;
            }
        }
        throw new GradleException(message);
    }
}

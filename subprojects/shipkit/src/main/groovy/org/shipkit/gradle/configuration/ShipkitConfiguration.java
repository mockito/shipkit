package org.shipkit.gradle.configuration;

import org.shipkit.internal.gradle.configuration.ShipkitConfigurationStore;
import org.shipkit.internal.util.EnvVariables;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.shipkit.internal.gradle.util.team.TeamParser.validateTeamMembers;
import static org.shipkit.internal.util.ArgumentValidation.notNull;

/**
 * Shipkit configuration.
 * Contains configuration that is used by Shipkit plugins to configure Shipkit tasks.
 * <p>
 * Example of a release configuration of a working example project
 * <a href="https://github.com/mockito/shipkit-example/blob/master/gradle/shipkit.gradle">on GitHub</a>.
 * <p>
 * Sophisticated example based on Mockito project:
 * <a href="https://github.com/mockito/mockito/blob/release/2.x/gradle/shipkit.gradle">Mockito project</a>.
 */
public class ShipkitConfiguration {

    private final ShipkitConfigurationStore store;

    private final GitHub gitHub = new GitHub();
    private final Javadoc javadoc = new Javadoc();
    private final ReleaseNotes releaseNotes = new ReleaseNotes();
    private final Git git = new Git();
    private final Team team = new Team();
    private final Android android = new Android();

    private String previousReleaseVersion;
    private boolean dryRun;

    public ShipkitConfiguration() {
        this(new EnvVariables());
    }

    ShipkitConfiguration(EnvVariables envVariables) {
        this(new ShipkitConfigurationStore(envVariables));

        //Configure default values
        git.setTagPrefix("v"); //so that tags are "v1.0", "v2.3.4"
        git.setReleasableBranchRegex("master|release/.+");  // matches 'master', 'release/2.x', 'release/3.x', etc.
        git.setCommitMessagePostfix("[ci skip]");
        git.setUser("shipkit-org");
        git.setEmail("<shipkit.org@gmail.com>");

        gitHub.setUrl("https://github.com");
        gitHub.setApiUrl("https://api.github.com");

        //It does not seem that write auth user is used by GitHub in any way
        gitHub.setWriteAuthUser("dummy");

        releaseNotes.setFile("docs/release-notes.md");
        releaseNotes.setIgnoreCommitsContaining(singletonList("[ci skip]"));
        releaseNotes.setLabelMapping(Collections.<String, String>emptyMap());
        releaseNotes.setPublicationPluginName("");

        team.setContributors(Collections.<String>emptyList());
        team.setDevelopers(Collections.<String>emptyList());
        team.setIgnoredContributors(Collections.<String>emptyList());
    }

    ShipkitConfiguration(ShipkitConfigurationStore store) {
        this.store = store;
    }

    /**
     * If the release steps should be invoked in "dry run" mode.
     * Relevant only to some kinds of release steps,
     * such as bintray upload, git push.
     */
    public boolean isDryRun() {
        return dryRun;
    }

    /**
     * See {@link #isDryRun()}
     */
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public GitHub getGitHub() {
        return gitHub;
    }

    public Javadoc getJavadoc() {
        return javadoc;
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

    public Android getAndroid() {
        return android;
    }

    /**
     * Return last previously released version number.
     */
    public String getPreviousReleaseVersion() {
        return previousReleaseVersion;
    }

    /**
     * See {@link #getPreviousReleaseVersion()}
     */
    public void setPreviousReleaseVersion(String previousReleaseVersion) {
        this.previousReleaseVersion = previousReleaseVersion;
    }

    /**
     * Provides 'lenient' copy of this configuration instance,
     * that does not fail fast when one accesses a property that is not configured (e.g. is null).
     * <p>
     * By default, release configuration object fails fast in this scenario.
     * This is a good default because it helps us identify missing configuration early.
     * However sometimes we want to check if the user has configured a property (like for GitHub write token) without failing.
     *
     * @return lenient copy of this configuration instance
     */
    public ShipkitConfiguration getLenient() {
        return new ShipkitConfiguration(store.getLenient());
    }

    public class GitHub {

        /**
         * GitHub URL address, for example: https://github.com.
         * Useful when you are using on-premises GitHub Enterprise
         * and your url is different than the default GitHub instance for Open Source
         */
        public String getUrl() {
            return store.getStringUrl("gitHub.url");
        }

        /**
         * See {@link #getUrl()}
         */
        public void setUrl(String url) {
            store.put("gitHub.url", url);
        }

        /**
         * GitHub API endpoint address, for example:  https://api.github.com
         */
        public String getApiUrl() {
            return store.getStringUrl("gitHub.apiUrl");
        }

        /**
         * See {@link #getApiUrl()}
         */
        public void setApiUrl(String apiUrl) {
            store.put("gitHub.apiUrl", apiUrl);
        }

        /**
         * GitHub repository name, for example: "mockito/shipkit"
         */
        public String getRepository() {
            return store.getString("gitHub.repository");
        }

        /**
         * See {@link #getRepository()}
         *
         * @param repository name of the repo, including user or organization section, for example: "mockito/shipkit"
         */
        public void setRepository(String repository) {
            store.put("gitHub.repository", repository);
        }

        /**
         * GitHub user associated with the write auth token.
         * Needed for the release process to push changes.
         */
        public String getWriteAuthUser() {
            return store.getString("gitHub.writeAuthUser");
        }

        /**
         * See {@link #getWriteAuthUser()}
         */
        public void setWriteAuthUser(String user) {
            store.put("gitHub.writeAuthUser", user);
        }

        /**
         * GitHub read only auth token.
         * Since the token is read-only it is ok to check that in to VCS.
         */
        public String getReadOnlyAuthToken() {
            return store.getString("gitHub.readOnlyAuthToken");
        }

        /**
         * See {@link #getReadOnlyAuthToken()}
         */
        public void setReadOnlyAuthToken(String token) {
            store.put("gitHub.readOnlyAuthToken", token);
        }

        /**
         * GitHub write auth token to be used for pushing code to GitHub.
         * Please do not configure write auth token in plain text / commit to VCS!
         * Instead use env variable and that you can securely store in CI server configuration.
         * Shipkit automatically uses "GH_WRITE_TOKEN" env variable
         * if this value is not specified.
         */
        public String getWriteAuthToken() {
            return (String) store.getValue("gitHub.writeAuthToken", "GH_WRITE_TOKEN", "Please export 'GH_WRITE_TOKEN' variable first!\n" +
                "It is highly recommended to keep write token secure and store env variable 'GH_WRITE_TOKEN' with your CI configuration. " +
                "Alternatively, you can configure GitHub write auth token explicitly (don't check this in to Git!):\n" +
                "  shipkit.gitHub.writeAuthToken = 'secret'");
        }

        /**
         * @see {@link #getWriteAuthToken()}
         */
        public void setWriteAuthToken(String writeAuthToken) {
            store.put("gitHub.writeAuthToken", writeAuthToken);
        }
    }

    public class Javadoc {
        /**
         * GitHub Javadoc repository name, for example: "mockito/shipkit-javadoc".
         * The default value is repository with "-javadoc" suffix.
         * <p>
         * To enable shipping Javadoc you need to apply Javadoc plugin first:
         * <pre>
         * apply plugin: "org.shipkit.javadoc"
         * </pre>
         * @since 2.2.0
         */
        public String getRepository() {
            return store.getString("javadoc.repository");
        }

        /**
         * @see {@link #getRepository()}
         * @since 2.2.0
         */
        public void setRepository(String javadocRepository) {
            store.put("javadoc.repository", javadocRepository);
        }

        /**
         * GitHub Javadoc repository branch name. The branch needs to exist.
         * By default it's using the branch set as main in GitHub repo, usually master.
         * <p>
         * To enable shipping Javadoc you need to apply Javadoc plugin first:
         * <pre>
         * apply plugin: "org.shipkit.javadoc"
         * </pre>
         * @since 2.2.0
         */
        public String getRepositoryBranch() {
            return store.getString("javadoc.repositoryBranch");
        }

        /**
         * @see {@link #getRepositoryBranch()}
         * @since 2.2.0
         */
        public void setRepositoryBranch(String javadocRepositoryBranch) {
            store.put("javadoc.repositoryBranch", javadocRepositoryBranch);
        }

        /**
         * GitHub Javadoc repository directory where put javadoc files. By default it's project root directory.
         * <p>
         * To enable shipping Javadoc you need to apply Javadoc plugin first:
         * <pre>
         * apply plugin: "org.shipkit.javadoc"
         * </pre>
         * @since 2.2.0
         */
        public String getRepositoryDirectory() {
            return store.getString("javadoc.repositoryDirectory");
        }

        /**
         * @see {@link #getRepositoryDirectory()}
         * @since 2.2.0
         */
        public void setRepositoryDirectory(String javadocRepositoryDirectory) {
            store.put("javadoc.repositoryDirectory", javadocRepositoryDirectory);
        }

        /**
         * Commit message used to commit Javadocs. Default: "Update current and ${version} Javadocs. [ci skip]"
         * You can override this message and ${version} will be replaced by currently build version.
         * You don't need to specify "[ci skip]" in your message - it will be added automatically.
         * <p>
         * To enable shipping Javadoc you need to apply Javadoc plugin first:
         * <pre>
         * apply plugin: "org.shipkit.javadoc"
         * </pre>
         * @since 2.2.0
         */
        public String getCommitMessage() {
            return store.getString("javadoc.commitMessage");
        }

        /**
         * @see {@link #getCommitMessage()}
         * @since 2.2.0
         */
        public void setCommitMessage(String commitMessage) {
            store.put("javadoc.commitMessage", commitMessage);
        }
    }

    public class ReleaseNotes {

        /**
         * Release notes file relative path, for example: "docs/release-notes.md"
         */
        public String getFile() {
            return store.getString("releaseNotes.file");
        }

        /**
         * See {@link #getFile()}
         */
        public void setFile(String file) {
            store.put("releaseNotes.file", file);
        }

        /**
         * Issue tracker label mappings.
         * The mapping of issue tracker labels (for example "GitHub label") to human readable and presentable name.
         * The order of labels is important and will influence the order
         * in which groups of issues are generated in release notes.
         * Examples: ['java-9': 'Java 9 support', 'BDD': 'Behavior-Driven Development support']
         */
        public Map<String, String> getLabelMapping() {
            return store.getMap("releaseNotes.labelMapping");
        }

        /**
         * See {@link #getLabelMapping()}
         */
        public void setLabelMapping(Map<String, String> labelMapping) {
            store.put("releaseNotes.labelMapping", labelMapping);
        }

        /**
         * Release notes are generated based on information in commit messages.
         * If a commit message contains any of texts from this collection,
         * that commit will be ignored and not used for generating release notes.
         */
        public Collection<String> getIgnoreCommitsContaining() {
            return store.getCollection("releaseNotes.ignoreCommitsContaining");
        }

        /**
         * See {@link #getIgnoreCommitsContaining()}
         */
        public void setIgnoreCommitsContaining(Collection<String> commitMessageParts) {
            store.put("releaseNotes.ignoreCommitsContaining", commitMessageParts);
        }

        /**
         * Get the Publication Repository
         *
         * @see #setPublicationRepository(String)
         */
        public String getPublicationRepository() {
            return store.getString("releaseNotes.publicationRepository");
        }

        /**
         * Set the Publication Repository where we look for your published binary. Version will be concatenated to it.
         * It is currently used to configure repository Badge URL when generating release notes.
         * E.g.
         * <pre>
         *   releaseNotes.publicationRepository = "https://plugins.gradle.org/plugin/org.shipkit.java/"
         * </pre>
         * For version = "1.2.3" will result in:
         * "https://plugins.gradle.org/plugin/org.shipkit.java/1.2.3"
         * This will be used for adding and linking the repository in the release notes.
         *
         * @see #getPublicationRepository()
         */
        public void setPublicationRepository(String publicationRepository) {
            store.put("releaseNotes.publicationRepository", publicationRepository);
        }

        /**
         * Get the Publication Plugin Name
         *
         * @see @setPublicationPluginName(String)
         * @since 2.0.32
         * @deprecated since 2.1.6 because we no longer are using this one. It is scheduled to be removed in 3.0.0.
         */
        @Deprecated
        public String getPublicationPluginName() {
            return store.getString("releaseNotes.publicationPluginName");
        }

        /**
         * Set the Publication Plugin Name published to Gradle Plugin Portal.
         * This is currently used to configure repository Badge URL when generating release notes.
         * E.g.
         * <pre>
         *     releaseNotes.publicationPluginName = "org.shipkit.java.gradle.plugin"
         * </pre>
         * and
         * <pre>
         *    releaseNotes.publicationRepository = "https://plugins.gradle.org/plugin/org.shipkit.java/"
         * </pre>
         * Will generate Gradle Badge:
         * <pre>
         *     https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/org/shipkit/java/org.shipkit.java.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=Gradle
         * </pre>
         * This will show nice badge with actual plugin version in Gradle Plugin Portal.
         *
         * @since 2.0.32
         *
         * @deprecated since 2.1.6 because we no longer are using this one. It is scheduled to be removed in 3.0.0.
         */
        @Deprecated
        public void setPublicationPluginName(String publicationPluginName) {
            store.put("releaseNotes.publicationPluginName", publicationPluginName);
        }
    }

    public class Git {

        /**
         * Git user to be used for automated commits made by release automation
         * (version bumps, release notes commits, etc.).
         * For example: "shipkit"
         */
        public String getUser() {
            return store.getString("git.user");
        }

        /**
         * See {@link #getUser()} ()}
         */
        public void setUser(String user) {
            store.put("git.user", user);
        }

        /**
         * Git email to be used for automated commits made by release automation
         * (version bumps, release notes commits, etc.).
         * For example "shipkit.org@gmail.com"
         */
        public String getEmail() {
            return store.getString("git.email");
        }

        /**
         * See {@link #getEmail()}
         */
        public void setEmail(String email) {
            store.put("git.email", email);
        }

        /**
         * Regex to be used to identify branches that are entitled to be released, for example "master|release/.+"
         */
        public String getReleasableBranchRegex() {
            return store.getString("git.releasableBranchRegex");
        }

        /**
         * See {@link #getReleasableBranchRegex()}
         */
        public void setReleasableBranchRegex(String releasableBranchRegex) {
            store.put("git.releasableBranchRegex", releasableBranchRegex);
        }

        /**
         * Prefix added to the version to create VCS-addressable tag,
         * for example: "v".
         * Empty string is ok and it means that there is not prefix.
         */
        public String getTagPrefix() {
            return store.getString("git.tagPrefix");
        }

        /**
         * See {@link #getTagPrefix()}
         */
        public void setTagPrefix(String tagPrefix) {
            store.put("git.tagPrefix", tagPrefix);
        }

        /**
         * Text which will be included in the commit message for all commits automatically created by the release
         * automation.
         * By default it is configured to append "[ci skip]" keyword which will prevent CI builds on Travis CI.
         */
        public String getCommitMessagePostfix() {
            return store.getString("git.commitMessagePostfix");
        }

        /**
         * See {@link #getCommitMessagePostfix()}
         */
        public void setCommitMessagePostfix(String commitMessagePostfix) {
            //TODO protect this setter and other relevant from invalid input (null value)
            store.put("git.commitMessagePostfix", commitMessagePostfix);
        }
    }

    /**
     * Team configuration
     */
    public class Team {

        /**
         * Developers to include in generated pom file.
         * It should be a collection of elements like "GITHUB_USER:FULL_NAME", example:
         * ['mockitoguy:Szczepan Faber', 'mstachniuk:Marcin Stachniuk'].
         * <p>
         * See POM reference for <a href="https://maven.apache.org/pom.html#Developers">Developers</a>.
         */
        public Collection<String> getDevelopers() {
            return store.getCollection("team.developers");
        }

        /**
         * See {@link #getDevelopers()}
         */
        public void setDevelopers(Collection<String> developers) {
            validateTeamMembers(developers);
            store.put("team.developers", developers);
        }

        /**
         * Contributors to include in generated pom file.
         * It should be a collection of elements like "GITHUB_USER:FULL_NAME", example:
         * ['mockitoguy:Szczepan Faber', 'mstachniuk:Marcin Stachniuk']
         * <p>
         * See POM reference for <a href="https://maven.apache.org/pom.html#Contributors">Contributors</a>.
         */
        public Collection<String> getContributors() {
            return store.getCollection("team.contributors");
        }

        /**
         * See {@link #getContributors()}
         */
        public void setContributors(Collection<String> contributors) {
            validateTeamMembers(contributors);
            store.put("team.contributors", contributors);
        }

        /**
         * Contributors to be ignored in release notes and generated pom file.
         * It should be VCS name (e.g. 'Szczepan Faber', 'shipkit-org', 'Marcin Stachniuk')
         * Ignored contributors takes precedence over contributors configuration
         */
        public Collection<String> getIgnoredContributors() {
            return store.getCollection("team.ignoredContributors");
        }

        /**
         * See {@link #getIgnoredContributors()}
         */
        public void setIgnoredContributors(Collection<String> ignoredContributors) {
            store.put("team.ignoredContributors", ignoredContributors);
        }
    }

    /**
     * Android library configuration
     */
    public class Android {
        /**
         * Artifact id of published AAR
         * For example: "shipkit"
         */
        public String getArtifactId() {
            return store.getString("android.artifactId");
        }

        /**
         * See {@link #getArtifactId()} ()}
         */
        public void setArtifactId(String artifactId) {
            notNull(artifactId, "artifactId");
            store.put("android.artifactId", artifactId);
        }
    }
}

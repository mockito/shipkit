package org.mockito.release.internal.gradle.util;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.mockito.release.gradle.ReleaseToolsProperties;

import java.util.Collection;
import java.util.Map;

import static org.mockito.release.gradle.ReleaseToolsProperties.gh_repository;

//TODO add:
// - documentation, unit tests
// - validation of presence of value
// - ability to be overridden by project parameters
// rename to ReleaseToolsSettings, figure out the overlap with EnvVariables
public class ExtContainer {

    private final static Logger LOG = Logging.getLogger(ExtContainer.class);

    private final ExtraPropertiesExtension ext;
    private final Project project;

    //TODO it would be nice if it was some kind of singleton, perhaps extension on root?
    public ExtContainer(Project project) {
        this.ext = project.getExtensions().getExtraProperties();
        this.project = project;
    }

    public Map<String, String> getMap(Object name) {
        return (Map<String, String>) getValue(name);
    }

    private Object getValue(Object name) {
        return ext.get(name.toString());
    }

    public String getString(Object name) {
        return (String) getValue(name);
    }

    public Collection<String> getCollection(Object name) {
        return (Collection<String>) getValue(name);
    }

    /**
     * If the release should be a dry run and avoid publishing to Bintray, GitHub, etc.
     */
    public boolean isReleaseDryRun() {
        return ext.has(ReleaseToolsProperties.releaseDryRun.toString());
    }

    /**
     * GitHub repository name, for example: "mockito/mockito"
     */
    public String getGitHubRepository() {
        return getString(gh_repository.toString());
    }

    /**
     * Release notes file relative path, for example: "docs/release-notes.md"
     */
    public String getReleaseNotesFile() {
        return getString(ReleaseToolsProperties.releaseNotes_file.toString());
    }

    /**
     * Returns Git generic user notation based on settings, for example:
     * "Mockito Release Tools &lt;mockito.release.tools@gmail.com&gt;"
     */
    public String getGitGenericUserNotation() {
        //TODO unit testable
        return getGitGenericUser() + " <" + getGitGenericEmail() + ">";
    }

    /**
     * Tag name to be used, "v" + project.version
     */
    public String getTag() {
        return "v" + project.getVersion();
    }

    /**
     * Returns the branch to work on by checking the env variable 'TRAVIS_BRANCH'
     */
    public String getCurrentBranch() {
        //TODO if not set, we should just call 'git branch' and parse the output. This will make things easier for local testing.
        return EnvVariables.getEnv("TRAVIS_BRANCH");
    }

    /**
     * Generic git user to be used for commits, for example "mockito.release.tools"
     */
    public String getGitGenericUser() {
        return getString("git_genericUser");
    }

    /**
     * Generic git email to be used for commits, for example "mockito.release.tools@gmail.com"
     */
    public String getGitGenericEmail() {
        return getString("git_genericEmail");
    }

    /**
     * Regex to be used to identify branches that entitled to be released, for example "master|release/.+"
     */
    public String getReleasableBranchRegex() {
        return getString("git_releasableBranchRegex");
    }

    /**
     * GitHub read only auth token
     */
    public String getGitHubReadOnlyAuthToken() {
        return getString(ReleaseToolsProperties.gh_readOnlyAuthToken);
    }

    /**
     * Notable release notes file, for example "docs/notable-release-notes.md"
     */
    public String getNotableReleaseNotesFile() {
        return getString("releaseNotes_notableFile");
    }
}

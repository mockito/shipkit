package org.mockito.release.internal.gradle.util;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.mockito.release.gradle.ReleaseToolsProperties;

import java.util.Collection;
import java.util.Map;

import static org.mockito.release.gradle.ReleaseToolsProperties.gh_repository;

//TODO add:
// - documentation, unit tests
// - validation of presence of value
// - ability to be overridden by project parameters

// rename to ReleaseToolsSettings
public class ExtContainer {

    private final ExtraPropertiesExtension ext;

    public ExtContainer(Project project) {
        this.ext = project.getExtensions().getExtraProperties();
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
     * Bintray repo name for upload
     */
    public String getBintrayRepo() {
        return getString("bintray_repo");
    }

    /**
     * GitHub repository name, for example: "mockito/mockito"
     */
    public String getGitHubRepository() {
        return getString(gh_repository.toString());
    }
}

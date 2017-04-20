package org.mockito.release.internal.gradle.util;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.mockito.release.gradle.ReleaseToolsProperties;

import java.util.Collection;
import java.util.Map;

//TODO add:
// - documentation, unit tests
// - validation of presence of value
// - ability to be overridden by project parameters
// rename to ReleaseToolsSettings, figure out the overlap with EnvVariables
public class ExtContainer {

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
     * Tag name to be used, "v" + project.version
     */
    public String getTag() {
        return "v" + project.getVersion();
    }

    /**
     * Notable release notes file, for example "docs/notable-release-notes.md"
     */
    public String getNotableReleaseNotesFile() {
        return getString("releaseNotes_notableFile");
    }
}

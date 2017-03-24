package org.mockito.release.internal.gradle;

import org.gradle.api.Project;

import java.util.Map;

//TODO add:
// - documentation, unit tests
// - validation of presence of value
// - ability to be overridden by project parameters
class ExtContainer {

    private final Project project;

    ExtContainer(Project project) {
        this.project = project;
    }

    Map<String, String> getMap(String name) {
        return (Map<String, String>) getValue(name);
    }

    Object getValue(String name) {
        return project.getExtensions().getExtraProperties().get(name);
    }

    public String getString(String name) {
        return (String) getValue(name);
    }
}

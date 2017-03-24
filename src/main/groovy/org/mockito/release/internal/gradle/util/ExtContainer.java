package org.mockito.release.internal.gradle.util;

import org.gradle.api.Project;

import java.util.Collection;
import java.util.Map;

//TODO add:
// - documentation, unit tests
// - validation of presence of value
// - ability to be overridden by project parameters
public class ExtContainer {

    private final Project project;

    public ExtContainer(Project project) {
        this.project = project;
    }

    public Map<String, String> getMap(Object name) {
        return (Map<String, String>) getValue(name);
    }

    private Object getValue(Object name) {
        return project.getExtensions().getExtraProperties().get(name.toString());
    }

    public String getString(Object name) {
        return (String) getValue(name);
    }

    public Collection<String> getCollection(Object name) {
        return (Collection<String>) getValue(name);
    }
}

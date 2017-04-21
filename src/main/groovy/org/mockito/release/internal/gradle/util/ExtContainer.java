package org.mockito.release.internal.gradle.util;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;

import java.util.Collection;

public class ExtContainer {

    private final ExtraPropertiesExtension ext;
    private final Project project;

    public ExtContainer(Project project) {
        this.ext = project.getExtensions().getExtraProperties();
        this.project = project;
    }

    private Object getValue(Object name) {
        return ext.get(name.toString());
    }

    public Collection<String> getCollection(Object name) {
        return (Collection<String>) getValue(name);
    }

    /**
     * Tag name to be used, "v" + project.version
     */
    public String getTag() {
        return "v" + project.getVersion();
    }
}

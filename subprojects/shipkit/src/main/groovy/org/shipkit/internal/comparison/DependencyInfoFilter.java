package org.shipkit.internal.comparison;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import java.util.*;

public class DependencyInfoFilter {

    private final String projectGroup;
    private final String previousVersion;
    private final String currentVersion;

    public DependencyInfoFilter(String projectGroup, String previousVersion, String currentVersion) {
        this.projectGroup = projectGroup;
        this.previousVersion = previousVersion;
        this.currentVersion = currentVersion;
    }

    /**
     * Takes {@param content} of {@value org.shipkit.internal.gradle.java.tasks.ComparePublications#DEPENDENCY_INFO_FILEPATH} file and:
     * - every json object inside is sorted alphabetically, so that it's easier to compare them
     * - every json array keeps its original order
     * - all submodules dependencies with the same {@link #projectGroup} and {@link #previousVersion}
     *   have their version replaced with {@link #currentVersion} so that they are not considered as differences
     *   when comparing current and previous {@value org.shipkit.internal.gradle.java.tasks.ComparePublications#DEPENDENCY_INFO_FILEPATH}
     */
    public String filter(String content) {
        JsonObject root = Jsoner.deserialize(content, new JsonObject());

        TreeMap<String, Object> sortedRoot = (TreeMap<String, Object>) toSortedJson(root);
        List<Object> dependencies = (List<Object>) sortedRoot.get("dependencies");


        for (Object dependency : dependencies) {
            TreeMap<String, Object> dep = (TreeMap<String, Object>) dependency;
            if (isSubmodule(dep)) {
                dep.put("version", currentVersion);
            }
        }

        return Jsoner.prettyPrint(Jsoner.serialize(sortedRoot));
    }

    /**
     * Reorders all jsonObjects inside, because original JsonObjects from json-simple use HashMap
     * which doesn't guarantee any order of elements
     */
    private Object toSortedJson(Object obj) {
        if (obj instanceof JsonObject) {
            TreeMap<String, Object> result = new TreeMap<String, Object>();
            for (Map.Entry<String, Object> child : ((JsonObject) obj).entrySet()) {
                result.put(child.getKey(), toSortedJson(child.getValue()));
            }
            return result;
        } else if (obj instanceof JsonArray) {
            ArrayList<Object> result = new ArrayList<Object>();
            for (Object child : (JsonArray) obj) {
                result.add(toSortedJson(child));
            }
            return result;
        } else {
            return obj;
        }
    }

    private boolean isSubmodule(TreeMap<String, Object> jsonDependency) {
        String group = (String) jsonDependency.get("group");
        String version = (String) jsonDependency.get("version");

        return projectGroup.equals(group) && version.equals(previousVersion);
    }
}

package org.shipkit.internal.gradle.versionupgrade;

public class DependencyNewVersionParser {

    public static final String DEPENDENCY_NEW_VERSION_PATTERN =
                "[A-Za-z0-9.\\-_]+:[A-Za-z0-9.\\-_]+:[0-9.]+";

    private final String dependencyNewVersion;

    public DependencyNewVersionParser(String dependencyNewVersion) {
        this.dependencyNewVersion = dependencyNewVersion;
    }

    public boolean isValid(){
        return dependencyNewVersion.matches(DEPENDENCY_NEW_VERSION_PATTERN);
    }

    public String getDependencyGroup(){
        return splitDependency()[0];
    }

    public String getDependencyName(){
        return splitDependency()[1];
    }

    public String getNewVersion(){
        return splitDependency()[2];
    }

    private String[] splitDependency(){
        return dependencyNewVersion.split(":");
    }
}

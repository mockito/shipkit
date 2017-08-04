package org.shipkit.internal.gradle.versionupgrade;

public class DependencyNewVersionParser {

    public static final String DEPENDENCY_NEW_VERSION_PATTERN =
                "[A-Za-z0-9.\\-_]+:[A-Za-z0-9.\\-_]+:[0-9.]+";

    private final String dependencyNewVersion;

    public DependencyNewVersionParser(String dependencyNewVersion) {
        this.dependencyNewVersion = dependencyNewVersion;
    }

    private boolean isValid(){
        return dependencyNewVersion.matches(DEPENDENCY_NEW_VERSION_PATTERN);
    }

    private String getDependencyGroup(){
        return splitDependency()[0];
    }

    private String getDependencyName(){
        return splitDependency()[1];
    }

    private String getNewVersion(){
        return splitDependency()[2];
    }

    private String[] splitDependency(){
        return dependencyNewVersion.split(":");
    }

    public void fillVersionUpgradeExtension(VersionUpgradeConsumerExtension versionUpgrade){
        if(dependencyNewVersion != null) {
            if(!isValid()){
                throw new IllegalArgumentException(
                    String.format("  Incorrect format of property 'dependency', it should match the pattern '%s', eg. 'org.shipkit:shipkit:1.2.3'.",
                        DEPENDENCY_NEW_VERSION_PATTERN));
            }
            versionUpgrade.setDependencyGroup(getDependencyGroup());
            versionUpgrade.setDependencyName(getDependencyName());
            versionUpgrade.setNewVersion(getNewVersion());
        }
    }
}

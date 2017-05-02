package org.mockito.release.internal.comparison;

public interface RemoteUrlResolver {
    /**
     * Implementations should return URL where artifact (eg. pom or jar) can be found
     * @param extension suffix of artifact (eg. ".pom" or "-sources.jar")
     */
    String resolveUrl(String projectGroup, String projectName, String version, String extension);
}

package org.shipkit.internal.comparison.artifact;

/**
 * To simplify the configuration of PublicationsComparator plugin
 * implementations of this interface should provide default URLs to find artifacts
 * Each implementation should
 */
public interface DefaultArtifactUrlResolver {
    /**
     * @param extension - suffix of the artifact, eg. '-sources.jar', '.pom'
     * @return absolute URL to find last previously released artifact with given
     */
    String getDefaultUrl(String extension);
}

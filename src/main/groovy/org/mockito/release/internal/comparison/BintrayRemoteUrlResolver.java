package org.mockito.release.internal.comparison;

class BintrayRemoteUrlResolver implements RemoteUrlResolver{
    /**
     * default remote URL - for Bintray repo
     * @return eg
     * https://bintray.com/shipkit/examples/download_file?file_path=/org/mockito/release-tools-example/api/0.15.1/api-0.15.1.pom";
     */
    @Override
    public String resolveUrl(String projectGroup, String projectName, String version, String extension) {
        return "https://bintray.com/shipkit/examples/download_file?file_path="
                    + projectGroup.replace(".", "/")
                    + "/" + projectName
                    + "/" + version
                    + "/" + projectName
                    + "-" + version + extension;
    }
}

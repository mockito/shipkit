package org.shipkit.internal.comparison.artifact;


import com.jfrog.bintray.gradle.BintrayExtension;

import org.gradle.api.Project;

class BintrayDefaultArtifactUrlResolver implements DefaultArtifactUrlResolver {

    private final String artifactBaseName;
    private final Project project;
    private final String previousVersion;

    BintrayDefaultArtifactUrlResolver(Project project, String artifactBaseName, String previousVersion) {
        this.project = project;
        this.artifactBaseName = artifactBaseName;
        this.previousVersion = previousVersion;
    }

    /**
     * @return URL of artifact in Bintray, eg:
     * "https://bintray.com/shipkit/examples/download_file?file_path=org/mockito/release-tools-example/api/0.15.0/api-0.15.0.jar"
     */
    @Override
    public String getDefaultUrl(String extension) {
        BintrayExtension bintrayExtension = project.getExtensions().getByType(BintrayExtension.class);

        String userOrgOrName = bintrayExtension.getPkg().getUserOrg();
        if (userOrgOrName == null) {
            userOrgOrName = bintrayExtension.getUser();
        }

        return String.format("https://bintray.com/%s/%s/download_file?file_path=%s/%s/%s/%s-%s%s",
            userOrgOrName,
            bintrayExtension.getPkg().getRepo(),
            project.getGroup().toString().replace('.', '/'),
            artifactBaseName,
            previousVersion,
            artifactBaseName,
            previousVersion,
            extension);
    }
}

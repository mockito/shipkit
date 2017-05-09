package org.mockito.release.internal.gradle.util.pom

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.publish.maven.MavenPublication
import org.mockito.release.gradle.ReleaseConfiguration

class PomCustomizer {

    private static final Logger LOG = Logging.getLogger(PomCustomizer)

    /**
     * Customizes the pom. The method requires following properties on root project to function correctly:
     */
    static void customizePom(Project project, ReleaseConfiguration conf, MavenPublication publication) {

        publication.pom.withXml {
            LOG.info("""  Customizing pom for publication '$publication.name' in project '$project.path'
    - Module name (project.archivesBaseName): $project.archivesBaseName
    - Description (project.description): $project.description
    - GitHub repository (project.rootProject.releasing.gitHub.repository): ${conf.getGitHub().getRepository()}
    - Developers (project.rootProject.releasing.team.developers): ${conf.team.developers.join(', ')}
    - Contributors (project.rootProject.releasing.team.contributors): ${conf.team.contributors.join(', ')}""")
            
            def root = asNode()

            PomXmlCustomizer.customizePom(root, conf, project.archivesBaseName, project.description)
        }
    }
}

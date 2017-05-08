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

            //Assumes project has java plugin applied. Pretty safe assumption
            root.appendNode('name', project.archivesBaseName)

            root.appendNode('packaging', 'jar')
            root.appendNode('url', "https://github.com/${conf.getGitHub().getRepository()}")
            root.appendNode('description', project.description)

            def license = root.appendNode('licenses').appendNode('license')
            license.appendNode('name', 'The MIT License')
            license.appendNode('url', "https://github.com/${conf.getGitHub().getRepository()}/blob/master/LICENSE")
            license.appendNode('distribution', 'repo')

            root.appendNode('scm').appendNode('url', "https://github.com/${conf.getGitHub().getRepository()}.git")

            def issues = root.appendNode('issueManagement')
            issues.appendNode('url', "https://github.com/${conf.getGitHub().getRepository()}/issues")
            issues.appendNode('system', 'GitHub issues')

            def ci = root.appendNode('ciManagement')
            ci.appendNode('url', "https://travis-ci.org/${conf.getGitHub().getRepository()}")
            ci.appendNode('system', 'TravisCI')

            //TODO use TeamCustomizer
            def developers = root.appendNode('developers')
            conf.team.developers.each {
                def split = it.split(':')
                assert split.length == 2
                def d = developers.appendNode('developer')
                d.appendNode('id', split[0])
                d.appendNode('name', split[1])
                d.appendNode('roles').appendNode('role', 'Core developer')
                d.appendNode('url', "https://github.com/${split[0]}")
            }

            if (!conf.team.contributors.isEmpty()) {
                def devs = new HashSet(conf.team.developers)
                def node = root.appendNode('contributors')
                conf.team.contributors.each {
                    if (!devs.contains(it)) {
                        def split = it.split(':')
                        assert split.length == 2
                        def d = node.appendNode('contributor')
                        d.appendNode('name', split[1])
                        d.appendNode('url', "https://github.com/${split[0]}")
                    }
                }
            }
        }
    }
}

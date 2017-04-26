package org.mockito.release.internal.gradle.util

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.publish.maven.MavenPublication
import org.mockito.release.gradle.ReleaseConfiguration
import org.mockito.release.notes.contributors.Contributors
import org.mockito.release.notes.contributors.ContributorsToPom

class PomCustomizer {

    private static final Logger LOG = Logging.getLogger(PomCustomizer)

    /**
     * Customizes the pom. The method requires following properties on root project to function correctly:
     *
     * <ul>
     *  <li> project.description
     *  <li> project.archivesBaseName
     *  <li> project.rootProject.releasing.gitHub.repository
     *  <li> project.rootProject.ext.pom_developers
     *  <li> project.rootProject.ext.pom_contributors
     * </ul>
     */
    static void customizePom(Project project, ReleaseConfiguration conf, MavenPublication publication) {

        /**
         * See issue https://github.com/mockito/mockito-release-tools/issues/36
         *
         * To implement automatic contributors in the pom, we would need something like (brainstorming):
         *  - getting all contributors for the project using GitHub api and feeding this plugin with it
         *  - parse the release notes file and just include all contributors we can find there :)
         *  - make the release generation create an additional file with release notes metadata in some structured format
         *      (like JSON or xml), then we could parse that file to get the contributors
         *  - make the release create additional file with contributors
         *  - the list of core developers would be static, but the list of contributors would grow
         */

        publication.pom.withXml {
            LOG.info("""  Customizing pom for publication '$publication.name' in project '$project.path'
    - Module name (project.archivesBaseName): $project.archivesBaseName
    - Description (project.description): $project.description
    - GitHub repository (project.rootProject.releasing.gitHub.repository): ${conf.getGitHub().getRepository()}
    - Developers (project.rootProject.releasing.releaseNotes.pomDevelopers): ${conf.team.developers.join(', ')}
    - Contributors (project.rootProject.releasing.releaseNotes.pomContributors): ${conf.team.contributors.join(', ')}""")
            
            def root = asNode()
            def rootProject = project.rootProject

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

            def contributorsNode = root.appendNode('contributors')
            def rootBuildDir = project.getRootProject().getBuildDir().getAbsolutePath()
            def contributorsPath = Contributors.getAllProjectContributorsFileName(rootBuildDir)
            ContributorsToPom.include(contributorsNode,
                    contributorsPath,
                    conf.team.contributors,
                    conf.team.developers,
                    conf.team.addContributorsToPomFromGitHub)
        }
    }
}

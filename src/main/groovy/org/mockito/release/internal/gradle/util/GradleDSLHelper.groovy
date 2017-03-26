package org.mockito.release.internal.gradle.util

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.publish.Publication
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension

/**
 * Useful to work around Gradle API that requires the use of Groovy.
 */
@CompileStatic
class GradleDSLHelper {

    /**
     * Needed because we cannot access publications or publishing normally via Java
     * Doing so will "resolve" the publications too early and things would not work
     * For example, pom would not have dependencies
     */
    @CompileDynamic
    static void publications(Project project, Action<PublicationContainer> action) {
        project.publishing {
            action.execute(publications)
        }
    }

    /**
     * Names of all publications found in this project
     */
    static List<String> publicationNames(Project project) {
        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class)
        return publishing.getPublications().collect { Publication it -> it.name }
    }
}

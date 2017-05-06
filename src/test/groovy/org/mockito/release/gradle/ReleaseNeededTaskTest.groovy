package org.mockito.release.gradle

import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.mockito.release.internal.comparison.PublicationsComparator
import spock.lang.Specification

class ReleaseNeededTaskTest extends Specification {

    ReleaseNeededTask underTest = new ProjectBuilder().build().getTasks().create("releaseNeeded", ReleaseNeededTask)

    def "allPublicationsEqual should be false if no PublicationComparisonTasks"() {
        when:
        underTest.releaseNeeded()

        then:
        !underTest.areAllPublicationsEqual()
    }

    def "allPublicationsEqual should be true if all PublicationComparisonTasks return true"() {
        given:
        def task = Mock(PublicationsComparator)
        task.isPublicationsEqual() >> true
        def task2 = Mock(PublicationsComparator)
        task2.isPublicationsEqual() >> true
        underTest.addPublicationsComparator(task)
        underTest.addPublicationsComparator(task2)

        when:
        underTest.releaseNeeded()

        then:
        underTest.areAllPublicationsEqual()
    }

    def "allPublicationsEqual should be false if one of PublicationComparisonTasks returns false"() {
        given:
        def task = Mock(PublicationsComparator)
        task.isPublicationsEqual() >> false
        def task2 = Mock(PublicationsComparator)
        task2.isPublicationsEqual() >> true
        underTest.addPublicationsComparator(task)
        underTest.addPublicationsComparator(task2)

        when:
        underTest.releaseNeeded()

        then:
        !underTest.areAllPublicationsEqual()
    }

    def "should fail if release not needed and mode is explosive"() {
        given:
        underTest.setExplosive(true)
        underTest.setPullRequest(true) // pullRequest == true makes notNeeded == true

        when:
        underTest.releaseNeeded()

        then:
        thrown(GradleException)
    }

    def "should NOT fail if release not needed and mode is NOT explosive"() {
        given:
        underTest.setExplosive(false)
        underTest.setPullRequest(true) // pullRequest == true makes notNeeded == true

        when:
        underTest.releaseNeeded()

        then:
        underTest.isReleaseNotNeeded()
    }
}

package org.mockito.release.gradle

import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.mockito.release.internal.comparison.PublicationsComparatorTask
import spock.lang.Specification

class ReleaseNeededTaskTest extends Specification {

    def tasks = new ProjectBuilder().build().getTasks();
    ReleaseNeededTask underTest = tasks.create("releaseNeeded", ReleaseNeededTask)

    def "allPublicationsEqual should be true if no PublicationComparisonTasks"() {
        when:
        underTest.releaseNeeded()

        then:
        underTest.areAllPublicationsEqual()
    }

    def "allPublicationsEqual should be true if all PublicationComparisonTasks return true"() {
        given:
        def task = tasks.create("compare1", PublicationsComparatorTask)
        task.setPublicationsEqual(true)
        def task2 = tasks.create("compare2", PublicationsComparatorTask)
        task2.setPublicationsEqual(true)
        underTest.addPublicationsComparator(task)
        underTest.addPublicationsComparator(task2)

        when:
        underTest.releaseNeeded()

        then:
        underTest.areAllPublicationsEqual()
    }

    def "allPublicationsEqual should be false if one of PublicationComparisonTasks returns false"() {
        given:
        def task = tasks.create("compare1", PublicationsComparatorTask)
        task.setPublicationsEqual(false)
        def task2 = tasks.create("compare2", PublicationsComparatorTask)
        task2.setPublicationsEqual(true)
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

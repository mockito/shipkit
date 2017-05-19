package org.mockito.release.gradle

import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.mockito.release.internal.comparison.PublicationsComparatorTask
import spock.lang.Specification
import spock.lang.Unroll

class ReleaseNeededTaskTest extends Specification {

    def tasks = new ProjectBuilder().build().getTasks();
    ReleaseNeededTask underTest = tasks.create("releaseNeeded", ReleaseNeededTask)

    @Unroll
    def "release is needed" (commitMessage, branch, pullRequest, publicationsComparatorsResults, releaseNotNeeded){
        given:
        underTest.setCommitMessage(commitMessage)
        underTest.setBranch(branch)
        underTest.setPullRequest(pullRequest)
        int i = 0;
        publicationsComparatorsResults.each{
            def task = tasks.create("" + i++, PublicationsComparatorTask)
            task.setPublicationsEqual(it)
            underTest.addPublicationsComparator(task)
        }
        underTest.setReleasableBranchRegex("master")

        expect:
        underTest.releaseNeeded()
        
        underTest.isReleaseNotNeeded() == releaseNotNeeded

        where:
        commitMessage       | branch    | pullRequest | publicationsComparatorsResults || releaseNotNeeded
        "message"           | "master"  | false       | [false, false]                 || false // base case (in all other cases only one parameter changes)

        null                | "master"  | false       | [false, false]                 || false // null commit msg
        " "                 | "master"  | false       | [false, false]                 || false // only whitespaces in commit msg
        "message"           | "master"  | false       | [true, false]                  || false  // not all publications equal

        "[ci skip-release]" | "master"  | false       | [false, false]                 || true  // skip-release in commit msg
        "message"           | "feature" | false       | [false, false]                 || true  // feature branch
        "message"           | null      | false       | [false, false]                 || true  // null branch
        "message"           | "master"  | true        | [false, false]                 || true  // pull request
        "message"           | "master"  | false       | [true, true]                   || true  // all publications equal
    }

    def "allPublicationsEqual should be false if no PublicationComparisonTasks"() {
        when:
        underTest.releaseNeeded()

        then:
        !underTest.areAllPublicationsEqual()
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

package org.shipkit.gradle

import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.comparison.PublicationsComparatorTask
import org.shipkit.internal.util.EnvVariables
import spock.lang.Specification
import spock.lang.Unroll

class ReleaseNeededTaskTest extends Specification {

    def tasks = new ProjectBuilder().build().getTasks();
    ReleaseNeededTask underTest = tasks.create("releaseNeeded", ReleaseNeededTask)

    @Unroll
    def "release is needed" (commitMessage, branch, pullRequest, skipEnvVar, publicationsComparatorsResults, releaseNeeded){
        given:
        underTest.setCommitMessage(commitMessage)
        underTest.setBranch(branch)
        underTest.setPullRequest(pullRequest)
        def envVariables = Mock(EnvVariables)
        envVariables.getenv("SKIP_RELEASE") >> skipEnvVar
        underTest.setEnvVariables(envVariables)
        int i = 0;
        publicationsComparatorsResults.each{
            def task = tasks.create("" + i++, PublicationsComparatorTask)
            task.setPublicationsEqual(it)
            underTest.addPublicationsComparator(task)
        }
        underTest.setReleasableBranchRegex("master")

        expect:
        underTest.releaseNeeded() == releaseNeeded

        where:
        commitMessage       | branch    | pullRequest |skipEnvVar | publicationsComparatorsResults || releaseNeeded
        "message"           | "master"  | false       | null      | [false, false]                 || true  // base case (in all other cases only one parameter changes)

        null                | "master"  | false       | null      | [false, false]                 || true  // null commit msg
        " "                 | "master"  | false       | null      | [false, false]                 || true  // only whitespaces in commit msg
        "message"           | "master"  | false       | null      | [true, false]                  || true  // not all publications equal

        "[ci skip-release]" | "master"  | false       | null      | [false, false]                 || false // skip-release in commit msg
        "message"           | "feature" | false       | null      | [false, false]                 || false // feature branch
        "message"           | null      | false       | null      | [false, false]                 || false // null branch
        "message"           | "master"  | true        | null      | [false, false]                 || false // pull request
        "message"           | "master"  | false       | "true"    | [false, false]                 || false // SKIP_RELEASE set
        "message"           | "master"  | false       | null      | [true, true]                   || false // all publications equal
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

        expect:
        !underTest.releaseNeeded()
    }
}

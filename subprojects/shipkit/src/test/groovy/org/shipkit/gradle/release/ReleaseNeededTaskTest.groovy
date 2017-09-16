package org.shipkit.gradle.release

import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.gradle.release.tasks.ReleaseNeeded
import org.shipkit.internal.util.EnvVariables
import spock.lang.Specification

class ReleaseNeededTaskTest extends Specification {

    def project = new ProjectBuilder().build()
    ReleaseNeededTask task = project.tasks.create("releaseNeeded", ReleaseNeededTask)

    def "release is needed" (commitMessage, branch, pullRequest, skipEnvVar, comparisonResults, releaseNeeded){
        given:
        task.setCommitMessage(commitMessage)
        task.setBranch(branch)
        task.setPullRequest(pullRequest)
        def envVariables = Mock(EnvVariables)
        envVariables.getNonEmptyEnv("SKIP_RELEASE") >> skipEnvVar
        comparisonResults.each {
            def f = File.createTempFile("shipkit-testing", "")
            f << it
            this.task.comparisonResults.add(f)
        }
        task.setReleasableBranchRegex("master")

        expect:
        new ReleaseNeeded().releaseNeeded(task, envVariables) == releaseNeeded

        where:
        commitMessage       | branch    | pullRequest |skipEnvVar | comparisonResults || releaseNeeded
        "message"           | "master"  | false       | null      | ["", "diff"]      || true  // base case (in all other cases only one parameter changes)

        null                | "master"  | false       | null      | ["", "diff"]      || true  // null commit msg
        " "                 | "master"  | false       | null      | ["", "diff"]      || true  // only whitespaces in commit msg
        "message"           | "master"  | false       | null      | ["", "diff"]      || true  // publications differ

        "[ci skip-release]" | "master"  | false       | null      | ["", "diff"]      || false // skip-release in commit msg
        "message"           | "feature" | false       | null      | ["", "diff"]      || false // feature branch
        "message"           | null      | false       | null      | ["", "diff"]      || false // null branch
        "message"           | "master"  | true        | null      | ["", "diff"]      || false // pull request
        "message"           | "master"  | false       | "true"    | ["", "diff"]      || false // SKIP_RELEASE set
        "message"           | "master"  | false       | null      | ["", ""]          || false  // publications are the same
        "message"           | "master"  | false       | null      | []                || true   // no comparison results
    }

    def "should fail if release not needed and mode is explosive"() {
        given:
        task.setExplosive(true)
        task.setPullRequest(true) // pullRequest == true makes notNeeded == true

        when:
        task.releaseNeeded()

        then:
        thrown(GradleException)
    }

    def "should NOT fail if release not needed and mode is NOT explosive"() {
        given:
        task.setExplosive(false)
        task.setPullRequest(true) // pullRequest == true makes notNeeded == true

        expect:
        !task.releaseNeeded()
    }
}

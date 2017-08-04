package org.shipkit.internal.gradle.git

import org.shipkit.internal.gradle.git.tasks.IdentifyGitOriginRepoTask
import org.shipkit.internal.util.ResultHandler
import testutil.PluginSpecification

class GitRemoteOriginPluginTest extends PluginSpecification {

    def "applies"() {
        when:
        project.plugins.apply(GitRemoteOriginPlugin)

        then:
        project.tasks.identifyGitOrigin
    }

    def "should call failure handler if there is an execution exception"() {
        given:
        project.plugins.apply(GitRemoteOriginPlugin)
        IdentifyGitOriginRepoTask task = project.tasks.identifyGitOrigin

        def exception = new RuntimeException("test")
        task.executionException = exception

        def resultHandler = Mock(ResultHandler)

        when:
        GitRemoteOriginPlugin.chooseHandlerForOriginResult(task, resultHandler)

        then:
        1 * resultHandler.onFailure(exception)
    }

    def "should call success handler if there is no execution exception"() {
        given:
        project.plugins.apply(GitRemoteOriginPlugin)
        IdentifyGitOriginRepoTask task = project.tasks.identifyGitOrigin

        def resultHandler = Mock(ResultHandler)

        conf.gitHub.writeAuthToken = "writeToken"
        conf.gitHub.writeAuthUser = "writeUser"
        task.originRepo = "mockito/shipkit"

        when:
        GitRemoteOriginPlugin.chooseHandlerForOriginResult(task, resultHandler)

        then:
        1 * resultHandler.onSuccess({GitRemoteOriginPlugin.GitOriginAuth auth ->
            auth.originRepositoryUrl == "https://writeUser:writeToken@github.com/mockito/shipkit.git" &&
            auth.originRepositoryName == "mockito/shipkit"})
    }


}

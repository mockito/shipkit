package org.shipkit.gradle.notes;

import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.notes.tasks.UpdateReleaseNotes;
import org.shipkit.internal.gradle.notes.tasks.UpdateReleaseNotesOnGitHub;
import org.shipkit.internal.util.GitHubApi;

/**
 * Remove incremental release notes from GitHub created by updateReleaseNotesOnGitHub task.
 * It's useful for local / downstreams testing for removing created release on GitHub.
 */
public class UpdateReleaseNotesOnGitHubCleanupTask extends UpdateReleaseNotesOnGitHubTask {

    /**
     * Remove release notes from GitHub release page created by updateReleaseNotesOnGitHub task.
     * @throws Exception
     */
    @TaskAction
    public void updateReleaseNotesOnGitHub() throws Exception {
        GitHubApi gitHubApi = new GitHubApi(getGitHubApiUrl(), getGitHubWriteToken());
        UpdateReleaseNotes updateReleaseNotes = new UpdateReleaseNotes();
        new UpdateReleaseNotesOnGitHub(gitHubApi, updateReleaseNotes)
            .removeReleaseNotes(this);
    }
}

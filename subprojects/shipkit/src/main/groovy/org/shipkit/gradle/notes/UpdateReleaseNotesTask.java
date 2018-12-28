package org.shipkit.gradle.notes;

import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.notes.tasks.UpdateReleaseNotes;
import org.shipkit.internal.notes.header.HeaderProvider;

/**
 * Generates incremental, detailed release notes text and appends them to the file {@link #getReleaseNotesFile()}.
 * When preview mode is enabled ({@link #isPreviewMode()}), the new release notes content is displayed only (file is not updated).
 */
public class UpdateReleaseNotesTask extends AbstractReleaseNotesTask {

    /**
     * Generates incremental release notes and appends it to the top of release notes file.
     */
    @TaskAction
    public void updateReleaseNotes() {
        new UpdateReleaseNotes().updateReleaseNotes(this, new HeaderProvider());
    }
}

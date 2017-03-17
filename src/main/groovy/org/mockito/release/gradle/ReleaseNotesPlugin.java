package org.mockito.release.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * The plugin adds following tasks:
 *
 * <ul>
 *     <li>updateReleaseNotes - updates release notes file in place.</li>
 *     <li>previewReleaseNotes - prints incremental release notes to the console for preview.</li>
 * </ul>
 *
 * The plugin also adds "notes" extension, for more information see {@link ReleaseNotesExtension}.
 */
public interface ReleaseNotesPlugin extends Plugin<Project> {
}

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
 * Requires following properties:
 * <ul>
 *     <li>{@link ReleaseToolsProperties#gh_repository}</li>
 *     <li>{@link ReleaseToolsProperties#gh_readOnlyAuthToken}</li>
 *     <li>{@link ReleaseToolsProperties#releaseNotes_file}</li>
 *     <li>Optional: {@link ReleaseToolsProperties#releaseNotes_labelMapping}</li>
 * </ul>
 */
public interface ReleaseNotesPlugin extends Plugin<Project> {

}

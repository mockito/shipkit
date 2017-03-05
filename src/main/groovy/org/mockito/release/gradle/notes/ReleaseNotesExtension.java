package org.mockito.release.gradle.notes;

/**
 * This extension object is added by {@link ReleaseNotesPlugin}. Example configuration:
 *
 * <pre>
 *  notes {
 *    notesFile = file("docs/release-notes.md")
 *    gitHubAuthToken = "secret"
 *    gitHubLabelMappings = [:]
 *  }
 * </pre>
 */
public interface ReleaseNotesExtension {
}

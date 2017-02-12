package org.mockito.release.notes.generator;

import java.util.Collection;

public interface ReleaseNotesParameters {

    String getTagPrefix();

    String getStartVersion();

    Collection<String> getTargetVersions();

    Collection<String> getLabels();
}

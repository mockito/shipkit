package org.mockito.release.notes.internal;

import org.mockito.release.notes.generator.ReleaseNotesParameters;

import java.util.Collection;

public class DefaultReleaseNotesParameters implements ReleaseNotesParameters {

    private final String startVersion;
    private final Collection<String> targetVersions;
    private final String tagPrefix;
    private final Collection<String> labels;

    DefaultReleaseNotesParameters(String startVersion, Collection<String> targetVersions,
                                  String tagPrefix, Collection<String> labels) {

        this.startVersion = startVersion;
        this.targetVersions = targetVersions;
        this.tagPrefix = tagPrefix;
        this.labels = labels;
    }

    public String getStartVersion() {
        return startVersion;
    }

    public Collection<String> getTargetVersions() {
        return targetVersions;
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public Collection<String> getLabels() {
        return labels;
    }
}
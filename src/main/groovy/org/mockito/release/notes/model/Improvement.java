package org.mockito.release.notes.model;

import java.util.Collection;

public interface Improvement {
    long getId();

    String getTitle();

    String getUrl();

    Collection<String> getLabels();
}

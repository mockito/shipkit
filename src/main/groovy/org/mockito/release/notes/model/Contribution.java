package org.mockito.release.notes.model;

import java.util.Collection;

public interface Contribution {
    Collection<Commit> getCommits();

    String getAuthorName();
}

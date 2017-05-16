package org.mockito.release.notes.vcs;

import org.json.simple.Jsonable;
import org.mockito.release.notes.model.Commit;

import java.io.Serializable;

public interface IgnoredCommit extends Jsonable, Serializable {
    boolean isTrue(Commit commit);
    String getType();
}

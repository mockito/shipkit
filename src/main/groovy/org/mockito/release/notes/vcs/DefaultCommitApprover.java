package org.mockito.release.notes.vcs;

import org.mockito.release.notes.model.Commit;
import org.mockito.release.notes.util.Predicate;

public class DefaultCommitApprover implements Predicate<Commit> {
    @Override
    public boolean isTrue(Commit commit) {
        final boolean ignored = new IgnoreCiSkip().isTrue(commit);
        return !ignored;
    }
}

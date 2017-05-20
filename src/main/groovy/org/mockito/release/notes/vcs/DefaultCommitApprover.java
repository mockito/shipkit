package org.mockito.release.notes.vcs;

import org.mockito.release.notes.model.Commit;
import org.mockito.release.notes.util.Predicate;

import java.util.Collection;
import java.util.LinkedList;

public class DefaultCommitApprover implements Predicate<Commit> {

    private Collection<Predicate<Commit>> ignoredCommits = new LinkedList<Predicate<Commit>>();

    public DefaultCommitApprover() {
        ignoredCommits.add(new IgnoreCiSkip());
    }

    public DefaultCommitApprover(String ignoreCommitMessagePostfix) {
        ignoredCommits.add(new IgnoreCiSkip());
        ignoredCommits.add(new IgnoreCommitByConfigMessagePostfix(ignoreCommitMessagePostfix));
    }

    @Override
    public boolean isTrue(Commit commit) {
        for (Predicate<Commit> ignoredCommit : ignoredCommits) {
            if (ignoredCommit.isTrue(commit)) {
                return false;
            }
        }
        return true;
    }
}

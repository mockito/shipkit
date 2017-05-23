package org.mockito.release.notes.vcs;

import org.mockito.release.notes.model.Commit;
import org.mockito.release.notes.util.Predicate;

import java.util.Collection;

public class CommitIgnored implements Predicate<Commit> {

    private Collection<String> commitMessagePartsToIgnore;

    public CommitIgnored(Collection<String> commitMessageParts) {
        this.commitMessagePartsToIgnore = commitMessageParts;
    }

    @Override
    public boolean isTrue(Commit commit) {
        for (String messagePartToIgnore : commitMessagePartsToIgnore) {
            if (commit.getMessage().contains(messagePartToIgnore)) {
                return true;
            }
        }
        return false;
    }
}

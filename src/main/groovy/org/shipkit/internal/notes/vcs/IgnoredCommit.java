package org.shipkit.internal.notes.vcs;

import org.shipkit.internal.notes.model.Commit;
import org.shipkit.internal.notes.util.Predicate;

import java.util.Collection;

public class IgnoredCommit implements Predicate<Commit> {

    private Collection<String> commitMessagePartsToIgnore;

    public IgnoredCommit(Collection<String> commitMessageParts) {
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

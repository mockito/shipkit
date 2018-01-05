package org.shipkit.internal.notes.vcs;

import org.shipkit.internal.notes.contributors.IgnoredContributor;
import org.shipkit.internal.notes.model.Commit;
import org.shipkit.internal.notes.util.Predicate;

import java.util.Collection;

public class IgnoredCommit implements Predicate<Commit> {

    private Collection<String> commitMessagePartsToIgnore;
    private IgnoredContributor ignoredContributor;

    public IgnoredCommit(Collection<String> commitMessageParts, IgnoredContributor ignoredContributor) {
        this.commitMessagePartsToIgnore = commitMessageParts;
        this.ignoredContributor = ignoredContributor;
    }

    @Override
    public boolean isTrue(Commit commit) {
        for (String messagePartToIgnore : commitMessagePartsToIgnore) {
            if (commit.getMessage().contains(messagePartToIgnore)) {
                return true;
            }
        }
        return ignoredContributor.isTrue(commit.getAuthorName());
    }
}

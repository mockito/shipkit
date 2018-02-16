package org.shipkit.internal.notes.vcs;

import org.shipkit.internal.notes.contributors.IgnoredContributor;
import org.shipkit.internal.notes.model.Commit;

import java.util.Collection;
import java.util.function.Predicate;

public class IgnoredCommit implements Predicate<Commit> {

    private Collection<String> commitMessagePartsToIgnore;
    private IgnoredContributor ignoredContributor;

    public IgnoredCommit(Collection<String> commitMessageParts, IgnoredContributor ignoredContributor) {
        this.commitMessagePartsToIgnore = commitMessageParts;
        this.ignoredContributor = ignoredContributor;
    }

    @Override
    public boolean test(Commit commit) {
        for (String messagePartToIgnore : commitMessagePartsToIgnore) {
            if (commit.getMessage().contains(messagePartToIgnore)) {
                return true;
            }
        }
        return ignoredContributor.test(commit.getAuthorName());
    }
}

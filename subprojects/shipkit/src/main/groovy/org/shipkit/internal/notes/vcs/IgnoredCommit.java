package org.shipkit.internal.notes.vcs;

import org.shipkit.internal.notes.model.Commit;
import org.shipkit.internal.notes.util.Predicate;

import java.util.Collection;

public class IgnoredCommit implements Predicate<Commit> {

    private Collection<String> commitMessagePartsToIgnore;
    private Collection<String> ignoredContributors;

    public IgnoredCommit(Collection<String> commitMessageParts, Collection<String> ignoredContributors) {
        this.commitMessagePartsToIgnore = commitMessageParts;
        this.ignoredContributors = ignoredContributors;
    }

    @Override
    public boolean isTrue(Commit commit) {
        for (String messagePartToIgnore : commitMessagePartsToIgnore) {
            if (commit.getMessage().contains(messagePartToIgnore)) {
                return true;
            }
        }

        for(String ignoredContributor : ignoredContributors){
            if(commit.getAuthorName().equals(ignoredContributor)){
                return true;
            }
        }

        return false;
    }
}

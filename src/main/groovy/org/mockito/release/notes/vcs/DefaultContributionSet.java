package org.mockito.release.notes.vcs;

import org.mockito.release.notes.util.Predicate;

import java.util.*;

public class DefaultContributionSet implements ContributionSet {

    private final List<Contribution> contributions = new LinkedList<Contribution>();

    private final Collection<Commit> commits = new LinkedList<Commit>();
    private final Predicate<Commit> ignoreCommit;
    private final Set<String> tickets = new LinkedHashSet<String>();

    public DefaultContributionSet(Predicate<Commit> ignoredCommit) {
        this.ignoreCommit = ignoredCommit;
    }

    public DefaultContributionSet add(Commit commit) {
        if (ignoreCommit.isTrue(commit)) {
            return this;
        }
        commits.add(commit);
        tickets.addAll(commit.getTickets());

        Contribution existing = findContribution(commit, contributions);
        if (existing != null) {
            existing.add(commit);
        } else {
            contributions.add(new Contribution(commit));
        }
        return this;
    }

    private static Contribution findContribution(Commit commit, Iterable<Contribution> contributions) {
        for (Contribution c : contributions) {
            //From Git Log we don't know the GitHub user ID, only the email and name.
            //Sometimes contributors have different email addresses while the same name
            //This leads to awkward looking release notes, where same author is shown multiple times
            //We consider the contribution to be the same if any of: email or name is the same
            //
            //This approach comes with a caveat. What if the user have same author name, different email and indeed it is a different user?
            // This scenario is not handled well but it is unlikely and we consider it a trade-off
            if (c.authorEmail.equals(commit.getAuthorEmail()) || c.authorName.equals(commit.getAuthorName())) {
                return c;
            }
        }
        return null;
    }

    public Collection<Commit> getAllCommits() {
        return commits;
    }

    public Collection<String> getAllTickets() {
        return tickets;
    }

    public Collection<Contribution> getContributions() {
        //sort the contributions by commits count
        //we need to do it at the end instead of keeping tree set field
        // because Contribution object is mutable and the tree will not reindex when an already-added element changes
        return new TreeSet<Contribution>(contributions);
    }
}
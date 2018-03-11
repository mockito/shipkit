package org.shipkit.internal.notes.vcs;

import org.json.simple.Jsoner;
import org.shipkit.internal.notes.model.Commit;
import org.shipkit.internal.notes.model.Contribution;
import org.shipkit.internal.notes.model.ContributionSet;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

class DefaultContributionSet implements ContributionSet {

    private static final String JSON_FORMAT = "{ \"commits\": %s }";

    private final List<DefaultContribution> contributions = new LinkedList<>();

    private final Collection<Commit> commits = new LinkedList<>();
    private final Set<String> tickets = new LinkedHashSet<>();

    public DefaultContributionSet add(Commit commit) {
        commits.add(commit);
        tickets.addAll(commit.getTickets());

        DefaultContribution existing = findContribution(commit, contributions);
        if (existing != null) {
            existing.add(commit);
        } else {
            contributions.add(new DefaultContribution(commit));
        }
        return this;
    }

    private static DefaultContribution findContribution(Commit commit, Iterable<DefaultContribution> contributions) {
        for (DefaultContribution c : contributions) {
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
        return new TreeSet<>(contributions);
    }

    public int getAuthorCount() {
        return contributions.size();
    }

    @Override
    public String toJson() {
        final String serializedCommits = Jsoner.serialize(commits);
        return String.format(JSON_FORMAT, serializedCommits);
    }

    @Override
    public void toJson(Writer writable) throws IOException {
        writable.append(toJson());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultContributionSet that = (DefaultContributionSet) o;

        if (contributions != null ? !contributions.equals(that.contributions) : that.contributions != null) {
            return false;
        }
        if (commits != null ? !commits.equals(that.commits) : that.commits != null) {
            return false;
        }
        return tickets != null ? tickets.equals(that.tickets) : that.tickets == null;
    }

    @Override
    public int hashCode() {
        int result = contributions != null ? contributions.hashCode() : 0;
        result = 31 * result + (commits != null ? commits.hashCode() : 0);
        result = 31 * result + (tickets != null ? tickets.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DefaultContributionSet{" +
                "contributions=" + contributions +
                ", commits=" + commits +
                ", tickets=" + tickets +
                '}';
    }
}

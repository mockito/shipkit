package org.mockito.release.notes.vcs;

import org.json.simple.Jsoner;
import org.mockito.release.notes.model.Commit;
import org.mockito.release.notes.model.Contribution;
import org.mockito.release.notes.util.CollectionSerializer;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

class DefaultContribution implements Contribution, Comparable<DefaultContribution> {

    private static final String JSON_FORMAT = "{ \"commits\": %s }";

    //email identifies the contributor, author alias not necessarily
    final String authorEmail;
    final String authorName;
    final List<Commit> commits = new LinkedList<Commit>();

    DefaultContribution(Commit commit) {
        authorEmail = commit.getAuthorEmail();
        authorName = commit.getAuthorName();
        commits.add(commit);
    }

    DefaultContribution add(Commit commit) {
        commits.add(commit);
        return this;
    }

    public int compareTo(DefaultContribution other) {
        int byCommitCount = Integer.valueOf(other.getCommits().size()).compareTo(commits.size());
        if (byCommitCount != 0) {
            return byCommitCount;
        }
        return this.authorName.toUpperCase().compareTo(other.authorName.toUpperCase());
    }

    @Override
    public Collection<Commit> getCommits() {
        return commits;
    }

    @Override
    public String getAuthorName() {
        return authorName;
    }

    public String toString() {
        return authorName + "/" + authorEmail + "(" + commits.size() + ")";
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
}

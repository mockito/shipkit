package org.mockito.release.notes.vcs;

import org.mockito.release.notes.model.Commit;
import org.mockito.release.notes.model.Contribution;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DefaultContribution implements Contribution, Comparable<DefaultContribution> {

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
}

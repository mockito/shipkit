package org.mockito.release.notes.vcs;

import org.mockito.release.notes.model.Commit;

import java.util.Collection;
import java.util.Set;

class GitCommit implements Commit {

    private final String commitId;
    private final String email;
    private final String author;
    private final String message;
    private final Set<String> tickets;

    GitCommit(String commitId, String email, String author, String message) {
        this.commitId = commitId;
        this.email = email;
        this.author = author;
        this.message = message;
        this.tickets = TicketParser.parseTickets(message);
    }

    @Override
    public String getCommitId() {
        return commitId;
    }

    public String getAuthorEmail() {
        return email;
    }

    public String getAuthorName() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public Collection<String> getTickets() {
        return tickets;
    }
}

package org.mockito.release.notes.vcs;

import org.json.simple.Jsoner;
import org.mockito.release.notes.model.Commit;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;

class GitCommit implements Commit {

    private static final String JSON_FORMAT = "{ \"commitId\": \"%s\", \"email\": \"%s\", \"author\": \"%s\", " +
            "\"message\": \"%s\" }";

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

    @Override
    public String toJson() {
        return String.format(JSON_FORMAT,
                Jsoner.escape(commitId),
                Jsoner.escape(email),
                Jsoner.escape(author),
                Jsoner.escape(message));
    }

    @Override
    public void toJson(Writer writable) throws IOException {
        writable.append(toJson());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GitCommit gitCommit = (GitCommit) o;

        if (commitId != null ? !commitId.equals(gitCommit.commitId) : gitCommit.commitId != null) return false;
        if (email != null ? !email.equals(gitCommit.email) : gitCommit.email != null) return false;
        if (author != null ? !author.equals(gitCommit.author) : gitCommit.author != null) return false;
        if (message != null ? !message.equals(gitCommit.message) : gitCommit.message != null) return false;
        return tickets != null ? tickets.equals(gitCommit.tickets) : gitCommit.tickets == null;

    }

    @Override
    public int hashCode() {
        int result = commitId != null ? commitId.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (tickets != null ? tickets.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GitCommit{" +
                "commitId='" + commitId + '\'' +
                ", email='" + email + '\'' +
                ", author='" + author + '\'' +
                ", message='" + message + '\'' +
                ", tickets=" + tickets +
                '}';
    }
}

package org.shipkit.internal.notes.vcs;

public class RevisionNotFoundException extends Exception {

    private String revision;

    RevisionNotFoundException(String message, String revision) {
        super(message);
        this.revision = revision;
    }

    RevisionNotFoundException(Throwable cause, String revision) {
        super(cause);
        this.revision = revision;
    }

    public String getRevision() {
        return revision;
    }
}

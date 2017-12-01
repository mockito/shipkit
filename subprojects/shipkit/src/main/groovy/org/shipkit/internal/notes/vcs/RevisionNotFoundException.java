package org.shipkit.internal.notes.vcs;

public class RevisionNotFoundException extends Exception {

    private String revision;

    RevisionNotFoundException(String revision) {
        this.revision = revision;
    }

    RevisionNotFoundException(String message, String revision) {
        super(message);
        this.revision = revision;
    }

    public String getRevision() {
        return revision;
    }
}

package org.mockito.release.notes.vcs;

import org.mockito.release.notes.model.Commit;

import java.io.IOException;
import java.io.Writer;

/**
 * Ignores commits with [ci skip]
 */
class IgnoreCiSkip implements IgnoredCommit {

    public final static String TYPE = "ignoreCiCommitType";

    private static final String JSON_FORMAT = "{ \"type\": \"%s\" }";

    public boolean isTrue(Commit commit) {
        //we used #id for Travis CI build number in commits performed by Travis. Let's avoid pulling those ids here.
        //also, if ci was skipped we probably are not interested in such change, no?
        //Currently, all our [ci skip] are infrastructure commits plus documentation changes made by humans via github web interface
        return commit.getMessage().contains("[ci skip]");
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toJson() {
        return String.format(JSON_FORMAT, TYPE);
    }

    @Override
    public void toJson(Writer writable) throws IOException {
        writable.append(toJson());
    }
}

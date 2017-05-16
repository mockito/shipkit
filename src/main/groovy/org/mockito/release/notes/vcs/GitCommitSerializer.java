package org.mockito.release.notes.vcs;

import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

public class GitCommitSerializer {
    public String serialize(GitCommit commit) {
        return commit.toJson();
    }

    public GitCommit deserialize(String jsonData) {
        try {
            final JsonObject jsonObject = (JsonObject) Jsoner.deserialize(jsonData);
            return deserialize(jsonObject);
        } catch (DeserializationException e) {
            throw new RuntimeException("Can't deserialize JSON: " + jsonData, e);
        }
    }

    public GitCommit deserialize(JsonObject jsonObject) {
        final String commitId = jsonObject.getString("commitId");
        final String email = jsonObject.getString("email");
        final String author = jsonObject.getString("author");
        final String message = jsonObject.getString("message");
        return new GitCommit(commitId, email, author, message);
    }
}

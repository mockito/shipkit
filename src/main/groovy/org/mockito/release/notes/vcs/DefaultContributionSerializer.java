package org.mockito.release.notes.vcs;

import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.mockito.release.notes.model.Commit;

//TODO: NagRock Is this class needed?
public class DefaultContributionSerializer {
    private GitCommitSerializer gitCommitSerializer = new GitCommitSerializer();

    public DefaultContributionSerializer(GitCommitSerializer gitCommitSerializer) {
        this.gitCommitSerializer = gitCommitSerializer;
    }

    public String serialize(DefaultContribution contribution) {
        return contribution.toJson();
    }

    public DefaultContribution deserialize(String jsonData) {
        try {
            final JsonObject jsonObject = (JsonObject) Jsoner.deserialize(jsonData);
            return deserialize(jsonObject);
        } catch (DeserializationException e) {
            throw new RuntimeException("Can't deserialize JSON: " + jsonData, e);
        }
    }

    public DefaultContribution deserialize(JsonObject jsonObject) {
        JsonArray commits = jsonObject.getCollection("commits");
        if (commits.size() == 0) {
            throw new RuntimeException("Contribution got to have at least one commit. Can't deserialize JSON: " + jsonObject.toJson());
        }
        return createDefaultContributionWithCommits(commits);
    }

    private DefaultContribution createDefaultContributionWithCommits(JsonArray commits) {
        final JsonObject firstCommit = (JsonObject) commits.get(0);
        DefaultContribution defaultContribution = new DefaultContribution(gitCommitSerializer.deserialize(firstCommit));
        for (int i = 1; i < commits.size(); i++) {
            Commit commit = gitCommitSerializer.deserialize((JsonObject) commits.get(i));
            defaultContribution.add(commit);
        }
        return defaultContribution;
    }
}

package org.mockito.release.notes.vcs;

import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.mockito.release.notes.model.Commit;

public class DefaultContributionSetSerializer {

    private GitCommitSerializer gitCommitSerializer = new GitCommitSerializer();

    public DefaultContributionSetSerializer(GitCommitSerializer gitCommitSerializer) {
        this.gitCommitSerializer = gitCommitSerializer;
    }

    public String serialize(DefaultContributionSet defaultContributionSet) {
        return defaultContributionSet.toJson();
    }

    public DefaultContributionSet deserialize(String jsonData) {
        try {
            final JsonObject jsonObject = (JsonObject) Jsoner.deserialize(jsonData);
            return deserialize(jsonObject);
        } catch (DeserializationException e) {
            throw new RuntimeException("Can't deserialize JSON: " + jsonData, e);
        }
    }

    public DefaultContributionSet deserialize(JsonObject jsonObject) {
        JsonArray commits = jsonObject.getCollection("commits");
        return createDefaultContributionWithCommits(commits);
    }

    private DefaultContributionSet createDefaultContributionWithCommits(JsonArray commits) {
        DefaultContributionSet defaultContributionSet = new DefaultContributionSet(new IgnoreCiSkip());
        for (int i = 0; i < commits.size(); i++) {
            Commit commit = gitCommitSerializer.deserialize((JsonObject) commits.get(0));
            defaultContributionSet.add(commit);
        }
        return defaultContributionSet;
    }
}

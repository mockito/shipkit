package org.shipkit.notes.vcs;

import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.notes.model.Commit;

public class DefaultContributionSetSerializer {

    private GitCommitSerializer gitCommitSerializer = new GitCommitSerializer();

    public DefaultContributionSetSerializer() {
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
        DefaultContributionSet defaultContributionSet = new DefaultContributionSet();
        JsonArray commits = jsonObject.getCollection("commits");
        addCommits(defaultContributionSet, commits);
        return defaultContributionSet;
    }

    private DefaultContributionSet addCommits(DefaultContributionSet defaultContributionSet, JsonArray commits) {
        for (Object commit : commits) {
            Commit gitCommit = gitCommitSerializer.deserialize((JsonObject) commit);
            defaultContributionSet.add(gitCommit);
        }
        return defaultContributionSet;
    }
}

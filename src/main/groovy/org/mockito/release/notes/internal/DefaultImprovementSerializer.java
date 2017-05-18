package org.mockito.release.notes.internal;

import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import java.util.Collection;

public class DefaultImprovementSerializer {

    public String serialize(DefaultImprovement defaultImprovement) {
        return defaultImprovement.toJson();
    }

    public DefaultImprovement deserialize(String jsonData) {
        try {
            final JsonObject jsonObject = (JsonObject) Jsoner.deserialize(jsonData);
            return deserialize(jsonObject);
        } catch (DeserializationException e) {
            throw new RuntimeException("Can't deserialize JSON: " + jsonData, e);
        }
    }

    public DefaultImprovement deserialize(JsonObject jsonObject) {
        final Long id = jsonObject.getLong("id");
        final String title = jsonObject.getString("title");
        final String url = jsonObject.getString("url");
        final Collection<String> labels = jsonObject.getCollection("labels");
        final boolean isPullRequest = jsonObject.getBoolean("isPullRequest");
        return new DefaultImprovement(id, title, url, labels, isPullRequest);
    }
}

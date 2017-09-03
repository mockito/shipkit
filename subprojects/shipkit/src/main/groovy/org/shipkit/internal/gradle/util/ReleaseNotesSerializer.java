package org.shipkit.internal.gradle.util;

import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.notes.internal.DefaultImprovementSerializer;
import org.shipkit.internal.notes.internal.DefaultReleaseNotesData;
import org.shipkit.internal.notes.model.ContributionSet;
import org.shipkit.internal.notes.model.Improvement;
import org.shipkit.internal.notes.model.ReleaseNotesData;
import org.shipkit.internal.notes.vcs.DefaultContributionSetSerializer;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class ReleaseNotesSerializer {

    private DefaultContributionSetSerializer defaultContributionSetSerializer = new DefaultContributionSetSerializer();
    private DefaultImprovementSerializer defaultImprovementSerializer = new DefaultImprovementSerializer();

    public ReleaseNotesSerializer() {
    }

    public ReleaseNotesSerializer(DefaultContributionSetSerializer defaultContributionSetSerializer, DefaultImprovementSerializer defaultImprovementSerializer) {
        this.defaultContributionSetSerializer = defaultContributionSetSerializer;
        this.defaultImprovementSerializer = defaultImprovementSerializer;
    }

    public String serialize(Collection<ReleaseNotesData> releaseNotes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        final Iterator<ReleaseNotesData> iterator = releaseNotes.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next().toJson());
            if (iterator.hasNext()) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public Collection<ReleaseNotesData> deserialize(String jsonData) {
        try {
            final JsonArray jsonArray = (JsonArray) Jsoner.deserialize(jsonData);
            return deserialize(jsonArray);
        } catch (DeserializationException e) {
            throw new RuntimeException("Can't deserialize JSON: " + jsonData, e);
        }
    }

    public Collection<ReleaseNotesData> deserialize(JsonArray jsonArray) {
        Collection<ReleaseNotesData> result = new LinkedList<ReleaseNotesData>();
        final Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            final JsonObject jsonObject = (JsonObject) iterator.next();
            String version = jsonObject.getString("version");
            Date date = new Date(jsonObject.getLong("date"));
            ContributionSet contributionSet = defaultContributionSetSerializer.deserialize((JsonObject) jsonObject.get("contributions"));
            final Collection<JsonObject> improvementsJsonObjectCollection = jsonObject.getCollection("improvements");
            final Iterator<JsonObject> improvementsIterator = improvementsJsonObjectCollection.iterator();
            Collection<Improvement> improvements = new LinkedList<Improvement>();
            while (improvementsIterator.hasNext()) {
                final JsonObject next = improvementsIterator.next();
                improvements.add(defaultImprovementSerializer.deserialize(next));
            }
            String previousVersionTag = jsonObject.getString("previousVersionTag");
            String thisVersionTag = jsonObject.getString("thisVersionTag");

            final DefaultReleaseNotesData releaseNotesData = new DefaultReleaseNotesData(version, date, contributionSet, improvements, previousVersionTag, thisVersionTag);
            result.add(releaseNotesData);
        }
        return result;
    }
}

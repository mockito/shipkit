package org.mockito.release.notes.contributors;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.mockito.release.notes.model.ProjectContributor;

import java.util.Collection;

public class AllContributorsSerializer {

    private static final Logger LOG = Logging.getLogger(AllContributorsSerializer.class);

    public String serialize(ProjectContributorsSet contributorsSet) {
        Collection<ProjectContributor> allContributors = contributorsSet.getAllContributors();
        String json = Jsoner.serialize(allContributors);
        LOG.info("Serialize contributors to: {}", json);
        return json;
    }

    public ProjectContributorsSet deserialize(String json) {
        ProjectContributorsSet set = new DefaultProjectContributorsSet();
        try {
            LOG.info("Deserialize project contributors from: {}", json);
            JsonArray array = (JsonArray) Jsoner.deserialize(json);
            for (Object object : array) {
                JsonObject jsonObject = (JsonObject) object;
                String name = jsonObject.getString("name");
                String login = jsonObject.getString("login");
                String profileUrl = jsonObject.getString("profileUrl");
                Integer numberOfContributions = jsonObject.getInteger("numberOfContributions");
                set.addContributor(new DefaultProjectContributor(name, login, profileUrl, numberOfContributions));
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't deserialize JSON: " + json, e);
        }
        return set;
    }
}

package org.mockito.release.notes.contributors;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.mockito.release.notes.model.Contributor;
import org.mockito.release.notes.util.IOUtil;

import java.io.File;
import java.util.Collection;

public class ContributorsSerializer {

    private static final Logger LOG = Logging.getLogger(ContributorsSerializer.class);

    private final String filePath;
    private String json;

    public ContributorsSerializer(String filePath) {
        this.filePath = filePath;
    }

    public void serialize(ContributorsSet contributorsSet) {
        Collection<Contributor> allContributors = contributorsSet.getAllContributors();
        json = Jsoner.serialize(allContributors);
        LOG.info("Serializacja do " + json);
        IOUtil.writeFile(new File(filePath), json);
    }

    public ContributorsSet desrialize() {
        LOG.info("Deserializacja z " + json);
        ContributorsSet set = new DefaultContributorsSet();
        try {
            json = IOUtil.readFully(new File(filePath));
            JsonArray array = (JsonArray) Jsoner.deserialize(json);
            for (Object object : array) {
                JsonObject jsonObject = (JsonObject) object;
                String name = jsonObject.getString("name");
                String login = jsonObject.getString("login");
                String profileUrl = jsonObject.getString("profileUrl");
                set.addContributor(new DefaultContributor(name, login, profileUrl));
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't deserialize JSON: " + json, e);
        }
        return set;
    }
}

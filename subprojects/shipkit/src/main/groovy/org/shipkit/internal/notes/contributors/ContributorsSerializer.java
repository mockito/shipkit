package org.shipkit.internal.notes.contributors;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.notes.model.Contributor;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;
import java.util.Collection;

public class ContributorsSerializer {

    private static final Logger LOG = Logging.getLogger(ContributorsSerializer.class);

    private final File file;

    public ContributorsSerializer(File file) {
        this.file = file;
    }

    public void serialize(ContributorsSet contributorsSet) {
        Collection<Contributor> allContributors = contributorsSet.getAllContributors();
        String json = Jsoner.serialize(allContributors);
        LOG.info("Serialize contributors to: {}", json);
        IOUtil.writeFile(file, json);
    }

    public ContributorsSet deserialize() {
        String json = "";
        ContributorsSet set = new DefaultContributorsSet();
        try {
            json = IOUtil.readFully(file);
            LOG.info("Deserialize contributors from: {}", json);
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

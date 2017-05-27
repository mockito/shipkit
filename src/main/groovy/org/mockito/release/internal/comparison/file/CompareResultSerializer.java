package org.mockito.release.internal.comparison.file;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CompareResultSerializer {

    private static final Logger LOG = Logging.getLogger(CompareResultSerializer.class);

    public String serialize(CompareResult compareResult) {
        String json = Jsoner.serialize(compareResult);
        LOG.info("Serialize compare result to: {}", json);
        return json;
    }

    public CompareResult deserialize(String json) {
        CompareResult compareResult = new CompareResult();
        try {
            LOG.info("Deserialize compare result from: {}", json);
            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(json);
            Collection<String> onlyA = jsonObject.getCollection("onlyA");
            Collection<String> onlyB = jsonObject.getCollection("onlyB");
            Collection<String> both = jsonObject.getCollection("both");
            compareResult.setOnlyA(toFileList(onlyA));
            compareResult.setOnlyB(toFileList(onlyB));
            compareResult.setBothButDifferent(toFileList(both));
        } catch (DeserializationException e) {
            throw new RuntimeException("Can't deserialize JSON: " + json, e);
        }
        return compareResult;
    }

    private List<File> toFileList(Collection<String> onlyA) {
        List<File> fileList = new ArrayList<File>(onlyA.size());
        for (String filePath : onlyA) {
            fileList.add(new File(filePath));
        }
        return fileList;
    }

}

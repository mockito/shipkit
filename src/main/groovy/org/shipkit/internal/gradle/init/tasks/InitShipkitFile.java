package org.shipkit.internal.gradle.init.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.init.InitShipkitFileTask;
import org.shipkit.internal.notes.util.IOUtil;
import org.shipkit.internal.util.TemplateResolver;

import java.io.File;

public class InitShipkitFile {

    private static final Logger LOG = Logging.getLogger(InitShipkitFile.class);

    public void initShipkitFile(InitShipkitFileTask task) {
        File shipkitFile = task.getShipkitFile();
        String originRepoName = task.getOriginRepoName();
        initShipkitFile(shipkitFile, originRepoName);
    }

    static void initShipkitFile(File shipkitFile, String originRepoName) {
        if (shipkitFile.exists()) {
            LOG.lifecycle("  Shipkit file already exists, nothing to do: {}", shipkitFile.getPath());
        } else {
            createShipkitFile(shipkitFile, originRepoName);
            LOG.lifecycle("  Shipkit configuration created at {}!\n" +
                "  You can modify it manually. Remember to check it into VCS!", shipkitFile.getPath());
        }
    }

    static void createShipkitFile(File shipkitFile, String originRepoName) {
        String content =
            new TemplateResolver(DEFAULT_SHIPKIT_CONFIG_FILE_CONTENT)
                .withProperty("gitHub.repository", originRepoName)
                .withProperty("gitHub.readOnlyAuthToken", "76826c9ec886612f504d12fd4268b16721c4f85d")

                .withProperty("bintray.key", "7ea297848ca948adb7d3ee92a83292112d7ae989")
                .withProperty("bintray.pkg.repo", "bootstrap")
                .withProperty("bintray.pkg.user", "shipkit-bootstrap-bot")
                .withProperty("bintray.pkg.userOrg", "shipkit-bootstrap")
                .withProperty("bintray.pkg.name", "maven")
                .withProperty("bintray.pkg.licenses", "['MIT']")
                .withProperty("bintray.pkg.labels", "['continuous delivery', 'release automation', 'shipkit']")

                .resolve();

        IOUtil.writeFile(shipkitFile, content);
    }

    private static final String DEFAULT_SHIPKIT_CONFIG_FILE_CONTENT =
        "//This file was created automatically and is intended to be checked-in.\n" +
            "shipkit {\n" +
            "   gitHub.repository = \"@gitHub.repository@\"\n" +
            "\n" +
            "   //TODO when you finish trying out Shipkit, use your own token below (http://link/needed)\n" +
            "   gitHub.readOnlyAuthToken = \"@gitHub.readOnlyAuthToken@\"\n" +
            "}\n" +
            "\n" +
            "allprojects {\n" +
            "   plugins.withId(\"org.shipkit.bintray\") {\n" +
            "       //TODO when you finish trying out Shipkit, use your own Bintray repository below (http://link/needed)\n" +
            "       bintray {\n" +
            "           key = '@bintray.key@'\n" +
            "           pkg {\n" +
            "               repo = '@bintray.pkg.repo@'\n" +
            "               user = '@bintray.pkg.user@'\n" +
            "               userOrg = '@bintray.pkg.userOrg@'\n" +
            "               name = '@bintray.pkg.name@'\n" +
            "               licenses = @bintray.pkg.licenses@\n" +
            "               labels = @bintray.pkg.labels@\n" +
            "           }\n" +
            "       }\n" +
            "   }\n" +
            "}\n";
}

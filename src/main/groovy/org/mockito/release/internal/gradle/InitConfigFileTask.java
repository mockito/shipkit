package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.notes.util.IOUtil;

import java.io.File;

public class InitConfigFileTask extends DefaultTask{

    private static final Logger LOG = Logging.getLogger(InitConfigFileTask.class);

    private File configFile;

    @TaskAction public void initShipkitConfigFile(){
        if(configFile.exists()){
            LOG.lifecycle("  File {} already exists. Nothing to create.", configFile.getPath());
        } else{
            createShipKitConfigFile();
            LOG.lifecycle("  Created config file at {}. Please configure it.", configFile.getPath());
        }
    }

    private void createShipKitConfigFile() {
        String content =
                new TemplateResolver(DEFAULT_SHIPKIT_CONFIG_FILE_CONTENT)
                        .withProperty("gitHub.repository", "mockito/mockito-release-tools-example")
                        .withProperty("gitHub.writeAuthUser", "shipkit")
                        .withProperty("gitHub.readOnlyAuthToken", "e7fe8fcfd6ffedac384c8c4c71b2a48e646ed1ab")

                        .withProperty("bintray.pkg.repo", "examples")
                        .withProperty("bintray.pkg.user", "szczepiq")
                        .withProperty("bintray.pkg.userOrg", "shipkit")
                        .withProperty("bintray.pkg.name", "basic")
                        .withProperty("bintray.pkg.licenses", "['MIT']")
                        .withProperty("bintray.pkg.labels", "['continuous delivery', 'release automation', 'mockito']")

                        .resolve();

        IOUtil.writeFile(configFile, content);
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    static final String DEFAULT_SHIPKIT_CONFIG_FILE_CONTENT =
            "//This file was created automatically and is intended to be checked-in.\n" +
                    "releasing {\n"+
                    "   gitHub.repository = \"@gitHub.repository@\"\n"+
                    "   gitHub.readOnlyAuthToken = \"@gitHub.readOnlyAuthToken@\"\n"+
                    "   gitHub.writeAuthUser = \"@gitHub.writeAuthUser@\"\n"+
                    "}\n"+
                    "\n"+
                    "allprojects {\n"+
                    "   plugins.withId(\"org.mockito.mockito-release-tools.bintray\") {\n"+
                    "       bintray {\n"+
                    "           pkg {\n"+
                    "               repo = '@bintray.pkg.repo@'\n"+
                    "               user = '@bintray.pkg.user@'\n"+
                    "               userOrg = '@bintray.pkg.userOrg@'\n"+
                    "               name = '@bintray.pkg.name@'\n"+
                    "               licenses = @bintray.pkg.licenses@\n"+
                    "               labels = @bintray.pkg.labels@\n"+
                    "           }\n"+
                    "       }\n"+
                    "   }\n"+
                    "}\n";
}

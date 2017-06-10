package org.shipkit.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.notes.vcs.GitOriginRepoProvider;
import org.shipkit.internal.exec.DefaultProcessRunner;
import org.shipkit.internal.exec.ProcessRunner;
import org.shipkit.internal.notes.util.IOUtil;
import org.shipkit.internal.util.ExposedForTesting;

import java.io.File;

public class InitConfigFileTask extends DefaultTask{

    private static final Logger LOG = Logging.getLogger(InitConfigFileTask.class);

    private File configFile;
    private GitOriginRepoProvider gitOriginRepoProvider;
    public static final String FALLBACK_GITHUB_REPO = "mockito/mockito-release-tools-example";

    public InitConfigFileTask(){
        ProcessRunner runner = new DefaultProcessRunner(getProject().getProjectDir());
        gitOriginRepoProvider = new GitOriginRepoProvider(runner);
    }

    @TaskAction public void initShipkitConfigFile(){
        if(configFile.exists()){
            LOG.lifecycle("  Shipkit configuration already exists, nothing to do. Configuration file: {}", configFile.getPath());
        } else{
            createShipKitConfigFile();
            LOG.lifecycle("  Shipkit configuration created at {}! You can modify it manually. Remember to check it into VCS!", configFile.getPath());
        }
    }

    private void createShipKitConfigFile() {
        String defaultGitRepo = getOriginGitRepo();
        String content =
                new TemplateResolver(DEFAULT_SHIPKIT_CONFIG_FILE_CONTENT)
                        .withProperty("gitHub.repository", defaultGitRepo)
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

    private String getOriginGitRepo() {
        try {
            return gitOriginRepoProvider.getOriginGitRepo();
        } catch(Exception e){
            LOG.error("Failed to get url of git remote origin. Using fallback '" + FALLBACK_GITHUB_REPO + "' instead.\n" +
                    "You can change GitHub repository manually in " + configFile, e);
            return FALLBACK_GITHUB_REPO;
        }
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    @ExposedForTesting
    public void setGitOriginRepoProvider(GitOriginRepoProvider gitOriginRepoProvider) {
        this.gitOriginRepoProvider = gitOriginRepoProvider;
    }

    static final String DEFAULT_SHIPKIT_CONFIG_FILE_CONTENT =
            "//This file was created automatically and is intended to be checked-in.\n" +
                    "shipkit {\n"+
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

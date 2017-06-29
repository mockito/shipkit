package org.shipkit.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.exec.DefaultProcessRunner;
import org.shipkit.internal.exec.ProcessRunner;
import org.shipkit.internal.notes.util.IOUtil;
import org.shipkit.internal.notes.vcs.GitOriginRepoProvider;
import org.shipkit.internal.util.ExposedForTesting;

import java.io.File;

public class InitConfigFileTask extends DefaultTask{

    private static final Logger LOG = Logging.getLogger(InitConfigFileTask.class);

    private File configFile;
    private GitOriginRepoProvider gitOriginRepoProvider;
    public static final String FALLBACK_GITHUB_REPO = "mockito/shipkit-example";

    public InitConfigFileTask(){
        ProcessRunner runner = new DefaultProcessRunner(getProject().getProjectDir());
        gitOriginRepoProvider = new GitOriginRepoProvider(runner);
    }

    @TaskAction public void initShipkitConfigFile(){
        if(configFile.exists()){
            LOG.lifecycle("  Shipkit configuration already exists, nothing to do. Configuration file: {}", configFile.getPath());
        } else{
            createShipKitConfigFile();
            LOG.lifecycle("  Shipkit configuration created at {}!\n" +
                    "  You can modify it manually. Remember to check it into VCS!", configFile.getPath());
        }
    }

    private void createShipKitConfigFile() {
        String defaultGitRepo = getOriginGitRepo();
        String content =
                new TemplateResolver(DEFAULT_SHIPKIT_CONFIG_FILE_CONTENT)
                        .withProperty("gitHub.repository", defaultGitRepo)
                        .withProperty("gitHub.readOnlyAuthToken", "76826c9ec886612f504d12fd4268b16721c4f85d")

                        .withProperty("bintray.key", "7ea297848ca948adb7d3ee92a83292112d7ae989")
                        .withProperty("bintray.pkg.repo", "bootstrap")
                        .withProperty("bintray.pkg.user", "shipkit-bootstrap-bot")
                        .withProperty("bintray.pkg.userOrg", "shipkit-bootstrap")
                        .withProperty("bintray.pkg.name", "maven")
                        .withProperty("bintray.pkg.licenses", "['MIT']")
                        .withProperty("bintray.pkg.labels", "['continuous delivery', 'release automation', 'shipkit']")

                        .resolve();

        IOUtil.writeFile(configFile, content);
    }

    private String getOriginGitRepo() {
        try {
            return gitOriginRepoProvider.getOriginGitRepo();
        } catch (Exception e) {
            LOG.lifecycle("  Problems getting url of git remote origin (run with --debug to find out more).\n" +
                    "  Using fallback '" + FALLBACK_GITHUB_REPO + "' instead.\n" +
                    "  Please update GitHub repository in '" + configFile + "' file.\n");
            LOG.debug("  Problems getting url of git remote origin", e);
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
                    "shipkit {\n" +
                    "   gitHub.repository = \"@gitHub.repository@\"\n" +
                    "\n" +
                    "   //TODO when you finish trying out Shipkit, use your own token below (http://link/needed)\n" +
                    "   gitHub.readOnlyAuthToken = \"@gitHub.readOnlyAuthToken@\"\n"+
                    "}\n"+
                    "\n"+
                    "allprojects {\n"+
                    "   plugins.withId(\"org.shipkit.bintray\") {\n"+
                    "       //TODO when you finish trying out Shipkit, use your own Bintray repository below (http://link/needed)\n"+
                    "       bintray {\n"+
                    "           key = '@bintray.key@'\n"+
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

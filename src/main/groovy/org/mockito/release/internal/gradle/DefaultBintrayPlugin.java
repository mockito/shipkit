package org.mockito.release.internal.gradle;

import com.jfrog.bintray.gradle.BintrayExtension;
import com.jfrog.bintray.gradle.BintrayUploadTask;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.mockito.release.gradle.BintrayPlugin;
import org.mockito.release.internal.gradle.util.EnvVariables;
import org.mockito.release.internal.gradle.util.ExtContainer;
import org.mockito.release.internal.gradle.util.GradleDSLHelper;

import java.util.List;

public class DefaultBintrayPlugin implements BintrayPlugin {

    private final static Logger LOGGER = Logging.getLogger(DefaultBintrayPlugin.class);

    public void apply(final Project project) {
        //TODO since this plugin depends on bintray,
        // we need to either shade bintray plugin or ship this Gradle plugin in a separate jar
        project.getPlugins().apply("com.jfrog.bintray");
        project.getTasks().getByName("bintrayUpload").doFirst(new Action<Task>() {
            public void execute(Task task) {
                BintrayUploadTask t = (BintrayUploadTask) task;
                t.setApiKey(EnvVariables.getEnv("BINTRAY_API_KEY"));
                LOGGER.lifecycle("{} - publishing to Bintray\n" +
                    "  - dry run: {}\n" +
                    "  - repository: {}\n" +
                    "  - version: {}\n" +
                    "  - Maven Central sync: {}",
                    t.getPath(), t.getDryRun(), t.getRepoName(), t.getVersionName(), t.getSyncToMavenCentral());
            }
        });

        BintrayExtension bintray = project.getExtensions().getByType(BintrayExtension.class);
        ExtContainer ext = new ExtContainer(project.getRootProject());

        bintray.setPublish(true);
        bintray.setDryRun(ext.isReleaseDryRun());

        BintrayExtension.PackageConfig pkg = bintray.getPkg();
        pkg.setDesc(project.getDescription());
        pkg.setPublicDownloadNumbers(true);
        pkg.setRepo(ext.getBintrayRepo());
        pkg.setWebsiteUrl("https://github.com/" + ext.getGitHubRepository());
        pkg.setIssueTrackerUrl("https://github.com/" + ext.getGitHubRepository() + "/issues");
        pkg.setVcsUrl("https://github.com/" + ext.getGitHubRepository() + ".git");

        pkg.getVersion().setVcsTag("v" + project.getVersion());
        pkg.getVersion().getGpg().setSign(true);
    }
}

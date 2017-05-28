package org.mockito.release.internal.gradle;

import com.jfrog.bintray.gradle.BintrayExtension;
import com.jfrog.bintray.gradle.BintrayUploadTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.mockito.release.gradle.ReleaseConfiguration;

import static org.mockito.release.internal.gradle.configuration.BasicValidator.notNull;
import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;
import static org.mockito.release.internal.gradle.configuration.LazyConfiguration.lazyConfiguration;

/**
 * Applies and configures "com.jfrog.bintray" plugin based on sensible defaults
 * and user-defined values in "releasing" extension ({@link ReleaseConfiguration}).
 *
 * Applies plugins:
 * <ul>
 *     <li>{@link ReleaseConfigurationPlugin} to the root project</li>
 *     <li>"com.jfrog.bintray" to this project</li>
 * </ul>
 *
 * Conifgures "com.jfrog.bintray" plugin:
 * <ul>
 *     <li>Sets extension property: 'bintray.publish = true'</li>
 * </ul>
 */
public class BintrayPlugin implements Plugin<Project> {

    /**
     * Name of the task that is configured by this plugin
     */
    static final String BINTRAY_UPLOAD_TASK = "bintrayUpload";

    private final static Logger LOG = Logging.getLogger(BintrayPlugin.class);

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        //TODO since this plugin depends on bintray,
        // we need to either shade bintray plugin or ship this Gradle plugin in a separate jar
        // this way we avoid version conflicts and any bintray dependencies for users who don't use bintray
        project.getPlugins().apply("com.jfrog.bintray");

        //Configure some properties right away
        final BintrayExtension bintray = project.getExtensions().getByType(BintrayExtension.class);
        LOG.info("Configuring bintray plugin to publish automatically ({}.bintray.publish = true)", project.getPath());
        bintray.setPublish(true);

        final BintrayUploadTask bintrayUpload = (BintrayUploadTask) project.getTasks().getByName(BINTRAY_UPLOAD_TASK);

        bintrayUpload.doFirst(new Action<Task>() {
            public void execute(Task task) {
                //TODO unit test
                BintrayUploadTask t = (BintrayUploadTask) task;

                //workaround for https://github.com/bintray/gradle-bintray-plugin/issues/170
                notNull(bintray.getUser(), "Missing 'bintray.user' value.\n" +
                      "  Please configure Bintray extension.");

                String welcomeMessage = uploadWelcomeMessage(t);
                LOG.lifecycle(welcomeMessage);
            }
        });


        final BintrayExtension.PackageConfig pkg = bintray.getPkg();
        pkg.setPublicDownloadNumbers(true);
        pkg.getVersion().getGpg().setSign(true);

        //Defer configuration of other properties
        deferredConfiguration(project, new Runnable() {
            public void run() {
                //Below overwrites prior value in case the user configured dry run directly on the bintray extension.
                //It should be ok.
                bintray.setDryRun(conf.isDryRun());

                if (pkg.getDesc() == null) {
                    pkg.setDesc(project.getDescription());
                }

                if (pkg.getVersion().getVcsTag() == null) {
                    pkg.getVersion().setVcsTag("v" + project.getVersion());
                }

                if (pkg.getWebsiteUrl() == null) {
                    pkg.setWebsiteUrl("https://github.com/" + conf.getGitHub().getRepository());
                }

                if (pkg.getIssueTrackerUrl() == null) {
                    pkg.setIssueTrackerUrl("https://github.com/" + conf.getGitHub().getRepository() + "/issues");
                }

                if (pkg.getVcsUrl() == null) {
                    pkg.setVcsUrl("https://github.com/" + conf.getGitHub().getRepository() + ".git");
                }
            }
        });

        //TODO unit test, create static wrapper over env variables that we use for testing
        lazyConfiguration(bintrayUpload, new Runnable() {
            public void run() {
                String key = notNull(bintray.getKey(), "BINTRAY_API_KEY",
                        "Missing 'bintray.key' value.\n" +
                        "  Please configure Bintray extension or export 'BINTRAY_API_KEY' env variable.");
                bintray.setKey(key);
            }
        });
    }

    static String uploadWelcomeMessage(BintrayUploadTask t) {
        //TODO unit test
        return t.getPath() + " - publishing to Bintray\n" +
                            "  - dry run: " + t.getDryRun()
                            + ", version: " + t.getVersionName()
                            + ", Maven Central sync: " + t.getSyncToMavenCentral() + "\n" +
                            "  - user/org: " + t.getUser() + "/" + t.getUserOrg()
                            + ", repository/package: " + t.getRepoName() + "/" + t.getPackageName();
    }
}

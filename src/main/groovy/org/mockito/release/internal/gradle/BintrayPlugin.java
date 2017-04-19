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

import java.util.concurrent.Callable;

import static org.mockito.release.internal.gradle.ReleaseConfigurationPlugin.BINTRAY_KEY_ENV;
import static org.mockito.release.internal.gradle.configuration.BasicValidator.notNull;
import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;
import static org.mockito.release.internal.gradle.configuration.LazyValidator.lazyValidation;

/**
 * Applies and configures "com.jfrog.bintray" plugin based on sensible defaults
 * and user-defined values in "releasing" extension ({@link ReleaseConfiguration}).
 *
 * Applies plugins:
 * <ul>
 *     <li>{@link ReleaseConfigurationPlugin} to the root project</li>
 *     <li>"com.jfrog.bintray" to this project</li>
 * </ul>
 */
public class BintrayPlugin implements Plugin<Project> {

    /**
     * Name of the task that is configured by this plugin
     */
    static final String BINTRAY_UPLOAD_TASK = "bintrayUpload";

    private final static Logger LOGGER = Logging.getLogger(BintrayPlugin.class);

    public void apply(final Project project) {
        project.getRootProject().getPlugins().apply(ReleaseConfigurationPlugin.class);
        final ReleaseConfiguration conf = (ReleaseConfiguration) project.getRootProject().getExtensions()
                .getByName(ReleaseConfigurationPlugin.EXTENSION_NAME);

        //TODO since this plugin depends on bintray,
        // we need to either shade bintray plugin or ship this Gradle plugin in a separate jar
        // this way we avoid version conflicts and any bintray dependencies for users who don't use bintray
        project.getPlugins().apply("com.jfrog.bintray");

        final BintrayUploadTask bintrayUpload = (BintrayUploadTask) project.getTasks().getByName(BINTRAY_UPLOAD_TASK);

        bintrayUpload.doFirst(new Action<Task>() {
            public void execute(Task task) {
                //TODO unit test
                BintrayUploadTask t = (BintrayUploadTask) task;
                String welcomeMessage = uploadWelcomeMessage(t);
                LOGGER.lifecycle(welcomeMessage);
            }
        });

        //Configure some properties right away
        final BintrayExtension bintray = project.getExtensions().getByType(BintrayExtension.class);
        bintray.setPublish(true);

        final BintrayExtension.PackageConfig pkg = bintray.getPkg();
        pkg.setPublicDownloadNumbers(true);
        pkg.getVersion().setVcsTag("v" + project.getVersion());
        pkg.getVersion().getGpg().setSign(true);

        //Defer configuration of other properties
        deferredConfiguration(project, new Action<Project>() {
            public void execute(Project project) {
                //workaround for https://github.com/bintray/gradle-bintray-plugin/issues/170
                notNull(bintray.getUser(), "Bintray 'user' setting is required.\n" +
                        "  Please configure Bintray extension ('bintray.user').");

                bintray.setDryRun(conf.isDryRun());

                if (bintray.getKey() == null) {
                    bintray.setKey(conf.getBintray().getApiKey());
                }

                if (pkg.getDesc() == null) {
                    pkg.setDesc(project.getDescription());
                }

                if (pkg.getWebsiteUrl() == null) {
                    pkg.setWebsiteUrl("https://github.com/" + ghRepo());
                }

                if (pkg.getIssueTrackerUrl() == null) {
                    pkg.setIssueTrackerUrl("https://github.com/" + ghRepo() + "/issues");
                }

                if (pkg.getVcsUrl() == null) {
                    pkg.setVcsUrl("https://github.com/" + ghRepo() + ".git");
                }
            }

            private String ghRepo() {
                return notNull(conf.getGitHub().getRepository(), "'releasing.gitHub.repository' setting is required.");
            }
        });

        //TODO unit test
        lazyValidation(bintrayUpload)
            .notNull("Bintray 'apiKey' setting is required.\n" +
                            "  For safety, it's best to configure it using '" + BINTRAY_KEY_ENV + "' environment variable.\n" +
                            "  You can also configure it on Bintray extension ('bintray.key') or releasing extension ('releasing.bintray.apiKey')",
                    new Callable() {
                        public Object call() throws Exception {
                            return bintray.getKey();
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

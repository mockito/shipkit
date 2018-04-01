package org.shipkit.internal.gradle.bintray;

import com.jfrog.bintray.gradle.BintrayExtension;
import com.jfrog.bintray.gradle.BintrayUploadTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.configuration.LazyConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;

import static org.shipkit.internal.gradle.configuration.BasicValidator.notNull;
import static org.shipkit.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;

/**
 * Applies and configures "com.jfrog.bintray" plugin based on sensible defaults
 * and user-defined values in "shipkit" extension ({@link ShipkitConfiguration}).
 * <p>
 * Applies plugins:
 * <ul>
 * <li>{@link ShipkitConfigurationPlugin} to the root project</li>
 * <li>"com.jfrog.bintray" to this project</li>
 * </ul>
 * <p>
 * Configures "com.jfrog.bintray" plugin with sensible defaults
 * and with values specified in Shipkit file.
 */
public class ShipkitBintrayPlugin implements Plugin<Project> {

    /**
     * Name of the task that is configured by this plugin
     */
    public static final String BINTRAY_UPLOAD_TASK = "bintrayUpload";

    private final static Logger LOG = Logging.getLogger(ShipkitBintrayPlugin.class);

    public void apply(final Project project) {
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        //TODO (maybe) since this plugin depends on bintray,
        // we need to either shade bintray plugin or ship this Gradle plugin in a separate jar
        // this way we avoid version conflicts and any bintray dependencies for users who don't use bintray
        project.getPlugins().apply("com.jfrog.bintray");

        //Configure some properties right away
        final BintrayExtension bintray = project.getExtensions().getByType(BintrayExtension.class);
        LOG.info("Configuring bintray plugin to publish automatically ({}.bintray.publish = true)", project.getPath());
        bintray.setPublish(true);

        final BintrayUploadTask bintrayUpload = (BintrayUploadTask) project.getTasks().getByName(BINTRAY_UPLOAD_TASK);

        bintrayUpload.doFirst(task -> {
            String welcomeMessage = uploadWelcomeMessage((BintrayUploadTask) task);
            LOG.lifecycle(welcomeMessage);
        });

        bintrayUpload.doLast(task -> {
                BintrayUploadTask bintrayUploadTask = (BintrayUploadTask)task;
                if ((bintrayUploadTask.getFileUploads() == null || bintrayUploadTask.getFileUploads().length == 0) &&
                    (bintrayUploadTask.getConfigurationUploads() == null || bintrayUploadTask.getConfigurationUploads().length == 0) &&
                    (bintrayUploadTask.getPublicationUploads() == null || bintrayUploadTask.getPublicationUploads().length == 0)) {
                    LOG.lifecycle("No artifacts have been published to bintray!");
                }
            }
        );


        final BintrayExtension.PackageConfig pkg = bintray.getPkg();
        pkg.setPublicDownloadNumbers(true);
        pkg.getVersion().getGpg().setSign(true);

        //Defer configuration of other properties
        deferredConfiguration(project, () -> {
            //Below overwrites prior value in case the user configured dry run directly on the bintray extension.
            //It should be ok.
            bintray.setDryRun(conf.isDryRun());

            if (pkg.getDesc() == null) {
                pkg.setDesc(project.getDescription());
            }

            if (pkg.getName() == null) {
                pkg.setName(project.getGroup().toString());
            }

            if (pkg.getVersion().getVcsTag() == null) {
                pkg.getVersion().setVcsTag(conf.getGit().getTagPrefix() + project.getVersion());
            }
        });

        if (pkg.getWebsiteUrl() == null) {
            pkg.setWebsiteUrl(conf.getGitHub().getUrl() + "/" + conf.getGitHub().getRepository());
        }

        if (pkg.getIssueTrackerUrl() == null) {
            pkg.setIssueTrackerUrl(conf.getGitHub().getUrl() + "/" + conf.getGitHub().getRepository() + "/issues");
        }

        if (pkg.getVcsUrl() == null) {
            pkg.setVcsUrl(conf.getGitHub().getUrl() + "/" + conf.getGitHub().getRepository() + ".git");
        }

        LazyConfiguration.lazyConfiguration(bintrayUpload, () -> {
            String key = notNull(bintray.getKey(), "BINTRAY_API_KEY",
                "Missing 'bintray.key' value.\n" +
                    "  Please configure Bintray extension or export 'BINTRAY_API_KEY' env variable.");
            bintray.setKey(key);
            // api key is set by Bintray plugin, based on 'bintray.key' value, before lazy configuration.
            // Hence we need to set it again here:
            bintrayUpload.setApiKey(key);

            //workaround for https://github.com/bintray/gradle-bintray-plugin/issues/170
            notNull(bintray.getUser(), "Missing 'bintray.user' value.\n" +
                "  Please configure Bintray extension.");
        });
    }

    static String uploadWelcomeMessage(BintrayUploadTask t) {
        return t.getPath() + " - publishing to Bintray\n" +
            "  - dry run: " + t.getDryRun()
            + ", version: " + t.getVersionName()
            + ", Maven Central sync: " + t.getSyncToMavenCentral() + "\n" +
            "  - user/org: " + t.getUser() + "/" + t.getUserOrg()
            + ", repository/package: " + t.getRepoName() + "/" + t.getPackageName();
    }
}

package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.mockito.release.gradle.IncrementalReleaseNotes;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.gradle.util.TaskMaker;
import org.mockito.release.version.VersionInfo;

import java.io.File;

import static java.util.Collections.singletonList;
import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;
import static org.mockito.release.internal.gradle.configuration.LazyConfiguration.lazyConfiguration;

/**
 * The plugin adds following tasks:
 *
 * <ul>
 *     <li>updateReleaseNotes - updates release notes file in place.</li>
 *     <li>previewReleaseNotes - prints incremental release notes to the console for preview.</li>
 *     <li>fetchNotableReleaseNotes - queries GitHub to get notable release notes data.</li>
 *     <li>updateNotableReleaseNotes - updates notable release notes file in place.</li>
 * </ul>
 */
public class ReleaseNotesPlugin implements Plugin<Project> {

    private static final String TEMP_SERIALIZED_NOTES_FILE = "/notableReleaseNotes.ser";

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        project.getPlugins().apply(VersioningPlugin.class);
        project.getPlugins().apply(ContributorsPlugin.class);

        TaskMaker.task(project, "updateReleaseNotes", IncrementalReleaseNotes.UpdateTask.class, new Action<IncrementalReleaseNotes.UpdateTask>() {
            public void execute(final IncrementalReleaseNotes.UpdateTask t) {
                t.setDescription("Updates release notes file.");
                preconfigureIncrementalNotes(t, project, conf);
            }
        });

        TaskMaker.task(project, "previewReleaseNotes", IncrementalReleaseNotes.PreviewTask.class, new Action<IncrementalReleaseNotes.PreviewTask>() {
            public void execute(final IncrementalReleaseNotes.PreviewTask t) {
                t.setDescription("Shows new incremental content of release notes. Useful for previewing the release notes.");
                preconfigureIncrementalNotes(t, project, conf);
            }
        });

        project.getTasks().create("fetchNotableReleaseNotes", NotableReleaseNotesFetcherTask.class, new Action<NotableReleaseNotesFetcherTask>() {
            public void execute(NotableReleaseNotesFetcherTask task) {
                final NotesGeneration gen = task.getNotesGeneration();
                preconfigureNotableNotes(project, gen);

                lazyConfiguration(task, new Runnable() {
                    public void run() {
                        configureNotableNotes(project, gen, conf);
                    }
                });
            }
        });

        project.getTasks().create("updateNotableReleaseNotes", NotableReleaseNotesGeneratorTask.class,
                new Action<NotableReleaseNotesGeneratorTask>() {
            public void execute(NotableReleaseNotesGeneratorTask task) {
                final NotesGeneration gen = task.getNotesGeneration();
                preconfigureNotableNotes(project, gen);

                task.dependsOn("fetchNotableReleaseNotes");

                lazyConfiguration(task, new Runnable() {
                    public void run() {
                        configureNotableNotes(project, gen, conf);
                    }
                });
            }
        });

        configureNotableReleaseNotes(project);
    }

    private static void preconfigureIncrementalNotes(final IncrementalReleaseNotes task, final Project project, final ReleaseConfiguration conf) {
        task.dependsOn("fetchLastContributorsFromGitHub");
        deferredConfiguration(project, new Runnable() {
            public void run() {
                task.setGitHubLabelMapping(conf.getReleaseNotes().getLabelMapping()); //TODO make it optional
                task.setReleaseNotesFile(project.file(conf.getReleaseNotes().getFile())); //TODO add sensible default
                task.setGitHubReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
                task.setGitHubRepository(conf.getGitHub().getRepository());
            }
        });
    }

    private static void preconfigureNotableNotes(Project project, NotesGeneration gen){
        gen.setGitHubLabels(singletonList("noteworthy"));
        gen.setGitWorkingDir(project.getRootDir());
        gen.setIntroductionText("Notable release notes:\n\n");
        gen.setOnlyPullRequests(true);
        gen.setTagPrefix("v");
        gen.setTemporarySerializedNotesFile(getTemporaryReleaseNotesFile(project));
    }

    private static void configureNotableNotes(Project project, NotesGeneration gen, ReleaseConfiguration conf) {
        gen.setGitHubReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
        gen.setGitHubRepository(conf.getGitHub().getRepository());
        gen.setOutputFile(project.file(conf.getReleaseNotes().getNotableFile()));
        gen.setVcsCommitsLinkTemplate("https://github.com/" + conf.getGitHub().getRepository() + "/compare/{0}...{1}");
        gen.setDetailedReleaseNotesLink(conf.getGitHub().getRepository() + "/blob/" + conf.getGit().getBranch() + "/" + conf.getReleaseNotes().getNotableFile());
    }

    private static File getTemporaryReleaseNotesFile(Project project){
        String path = project.getBuildDir()  + TEMP_SERIALIZED_NOTES_FILE;
        return project.file(path);
    }


    private static void configureNotableReleaseNotes(Project project) {
        //TODO when we make notable release notes optional, we can push that to a separate plugin
        //like notable-release-notes plugin or something like that
        //this way, the normal detailed release notes do not depend on versioning plugin
        //and our plugins are more flexible
        VersionInfo versionInfo = project.getExtensions().getByType(VersionInfo.class);
        NotableReleaseNotesGeneratorTask generatorTask = (NotableReleaseNotesGeneratorTask) project.getTasks().getByName("updateNotableReleaseNotes");
        NotableReleaseNotesFetcherTask fetcherTask = (NotableReleaseNotesFetcherTask) project.getTasks().getByName("fetchNotableReleaseNotes");

        generatorTask.getNotesGeneration().setTargetVersions(versionInfo.getNotableVersions());
        fetcherTask.getNotesGeneration().setTargetVersions(versionInfo.getNotableVersions());

        if (versionInfo.isNotableRelease()) {
            generatorTask.getNotesGeneration().setHeadVersion(project.getVersion().toString());
            fetcherTask.getNotesGeneration().setHeadVersion(project.getVersion().toString());
        }
    }
}

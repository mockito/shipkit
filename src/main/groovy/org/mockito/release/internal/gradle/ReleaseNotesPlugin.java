package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.specs.Spec;
import org.mockito.release.gradle.IncrementalReleaseNotes;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.gradle.ReleaseToolsProperties;
import org.mockito.release.internal.gradle.configuration.DeferredConfiguration;
import org.mockito.release.internal.gradle.util.ExtContainer;
import org.mockito.release.internal.gradle.util.TaskMaker;

import java.io.File;

import static java.util.Collections.singletonList;
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
        project.getPlugins().apply(ReleaseConfigurationPlugin.class);
        final ReleaseConfiguration conf = (ReleaseConfiguration) project.getRootProject().getExtensions()
                .getByName(ReleaseConfigurationPlugin.EXTENSION_NAME);

        project.getPlugins().apply(ContributorsPlugin.class);

        TaskMaker.task(project, "updateReleaseNotes", IncrementalReleaseNotes.UpdateTask.class, new Action<IncrementalReleaseNotes.UpdateTask>() {
            public void execute(final IncrementalReleaseNotes.UpdateTask t) {
                t.setDescription("Updates release notes file.");
                preconfigureIncrementalNotes(t, project);
            }
        });

        TaskMaker.task(project, "previewReleaseNotes", IncrementalReleaseNotes.PreviewTask.class, new Action<IncrementalReleaseNotes.PreviewTask>() {
            public void execute(final IncrementalReleaseNotes.PreviewTask t) {
                t.setDescription("Shows new incremental content of release notes. Useful for previewing the release notes.");
                preconfigureIncrementalNotes(t, project);
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
    }

    private static void preconfigureIncrementalNotes(final IncrementalReleaseNotes task, final Project project) {
        task.dependsOn("fetchContributorsFromGitHub");
        final ExtContainer ext = new ExtContainer(project);
        DeferredConfiguration.deferredConfiguration(project, new Runnable() {
            public void run() {
                task.setGitHubLabelMapping(ext.getMap(ReleaseToolsProperties.releaseNotes_labelMapping)); //TODO make it optional
                task.setReleaseNotesFile(project.file(ext.getReleaseNotesFile())); //TODO add sensible default
                task.setGitHubReadOnlyAuthToken(ext.getGitHubReadOnlyAuthToken());
                task.setGitHubRepository(ext.getString(ReleaseToolsProperties.gh_repository));
                //TODO, do we need below force?
                forceTaskToAlwaysGeneratePreview(task);
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
        ExtContainer ext = new ExtContainer(project);
        gen.setGitHubReadOnlyAuthToken(ext.getGitHubReadOnlyAuthToken());
        gen.setGitHubRepository(ext.getGitHubRepository());
        gen.setOutputFile(project.file(ext.getNotableReleaseNotesFile()));
        gen.setVcsCommitsLinkTemplate("https://github.com/" + ext.getGitHubRepository() + "/compare/{0}...{1}");
        gen.setDetailedReleaseNotesLink(ext.getGitHubRepository() + "/blob/" + conf.getGit().getBranch() + "/" + ext.getNotableReleaseNotesFile());
    }

    private static File getTemporaryReleaseNotesFile(Project project){
        String path = project.getBuildDir()  + TEMP_SERIALIZED_NOTES_FILE;
        return project.file(path);
    }

    private static void forceTaskToAlwaysGeneratePreview(IncrementalReleaseNotes task) {
        task.getOutputs().upToDateWhen(new Spec<Task>() {
            @Override
            public boolean isSatisfiedBy(Task element) {
                return false;
            }
        });
    }
}

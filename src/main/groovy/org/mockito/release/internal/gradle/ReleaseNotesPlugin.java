package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.mockito.release.gradle.IncrementalReleaseNotes;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.gradle.ReleaseNotesFetcherTask;
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
        final GitStatusPlugin.GitStatus gitStatus = project.getPlugins().apply(GitStatusPlugin.class).getGitStatus();
        project.getPlugins().apply(VersioningPlugin.class);
        project.getPlugins().apply(ContributorsPlugin.class);

        detailedReleaseNotes(project, conf);

        project.getTasks().create("fetchNotableReleaseNotes", NotableReleaseNotesFetcherTask.class, new Action<NotableReleaseNotesFetcherTask>() {
            public void execute(NotableReleaseNotesFetcherTask task) {
                final NotesGeneration gen = task.getNotesGeneration();
                preconfigureNotableNotes(project, gen);

                lazyConfiguration(task, new Runnable() {
                    public void run() {
                        configureNotableNotes(project, gen, conf, gitStatus);
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
                        configureNotableNotes(project, gen, conf, gitStatus);
                    }
                });
            }
        });

        configureNotableReleaseNotes(project);
    }

    private static void detailedReleaseNotes(final Project project, final ReleaseConfiguration conf) {
        final ReleaseNotesFetcherTask fetcher = TaskMaker.task(project, "fetchReleaseNotes", ReleaseNotesFetcherTask.class, new Action<ReleaseNotesFetcherTask>() {
            public void execute(final ReleaseNotesFetcherTask t) {
                t.setDescription("Fetches release notes data from Git and GitHub and serializes them to a file");
                t.setOutputFile(new File(project.getBuildDir(), "detailed-release-notes.ser"));

                deferredConfiguration(project, new Runnable() {
                    public void run() {
                        t.setGitHubReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
                        t.setGitHubRepository(conf.getGitHub().getRepository());
                        t.setPreviousVersion(conf.getPreviousReleaseVersion());
                        t.setSkipCommitMessagePostfix(conf.getGit().getCommitMessagePostfix());
                    }
                });
            }
        });



        TaskMaker.task(project, "updateReleaseNotes", IncrementalReleaseNotes.UpdateTask.class, new Action<IncrementalReleaseNotes.UpdateTask>() {
            public void execute(final IncrementalReleaseNotes.UpdateTask t) {
                t.setDescription("Updates release notes file.");
                configureDetailedNotes(t, fetcher, project, conf);
            }
        });

        TaskMaker.task(project, "previewReleaseNotes", IncrementalReleaseNotes.PreviewTask.class, new Action<IncrementalReleaseNotes.PreviewTask>() {
            public void execute(final IncrementalReleaseNotes.PreviewTask t) {
                t.setDescription("Shows new incremental content of release notes. Useful for previewing the release notes.");
                configureDetailedNotes(t, fetcher, project, conf);
            }
        });
    }

    private static void configureDetailedNotes(final IncrementalReleaseNotes task, final ReleaseNotesFetcherTask fetcher,
                                               final Project project, final ReleaseConfiguration conf) {
        task.dependsOn(fetcher, ContributorsPlugin.CONFIGURE_CONTRIBUTORS_TASK);
        deferredConfiguration(project, new Runnable() {
            public void run() {
                task.setReleaseNotesData(fetcher.getOutputFile());
                task.setDevelopers(conf.getTeam().getDevelopers());
                task.setContributors(conf.getTeam().getContributors());
                task.setGitHubLabelMapping(conf.getReleaseNotes().getLabelMapping()); //TODO make it optional
                task.setReleaseNotesFile(project.file(conf.getReleaseNotes().getFile())); //TODO add sensible default
                task.setGitHubRepository(conf.getGitHub().getRepository());
                task.setPreviousVersion(project.getExtensions().getByType(VersionInfo.class).getPreviousVersion());
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

    private static void configureNotableNotes(Project project, NotesGeneration gen, ReleaseConfiguration conf, GitStatusPlugin.GitStatus gitStatus) {
        gen.setGitHubReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
        gen.setGitHubRepository(conf.getGitHub().getRepository());
        gen.setOutputFile(project.file(conf.getReleaseNotes().getNotableFile()));
        gen.setVcsCommitsLinkTemplate("https://github.com/" + conf.getGitHub().getRepository() + "/compare/{0}...{1}");
        gen.setDetailedReleaseNotesLink(conf.getGitHub().getRepository() + "/blob/" + gitStatus.getBranch() + "/" + conf.getReleaseNotes().getNotableFile());
        gen.setSkipCommitMessagePostfix(conf.getGit().getCommitMessagePostfix());
        gen.setIgnoreCommitsContaining(conf.getReleaseNotes().getIgnoreCommitsContaining());
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

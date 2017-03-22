package org.mockito.release.internal.gradle.pom;

import groovy.lang.Closure;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.publish.maven.MavenPublication;
import org.mockito.release.gradle.PomPlugin;

/**
 * Documentation for this method needs to be kept in {@link org.mockito.release.gradle.PomPlugin}
 */
public class DefaultPomPlugin implements PomPlugin, Plugin<Project> {

    public void apply(final Project project) {

        /**
         * See issue https://github.com/mockito/mockito-release-tools/issues/36
         *
         * To implement automatic contributors in the pom, we would need something like (brainstorming):
         *  - getting all contributors for the project using GitHub api and feeding this plugin with it
         *  - parse the release notes file and just include all contributors we can find there :)
         *  - make the release generation create an additional file with release notes metadata in some structured format
         *      (like JSON or xml), then we could parse that file to get the contributors
         *  - make the release create additional file with contributors
         *  - the list of core developers would be static, but the list of contributors would grow
         */
        project.getExtensions().getExtraProperties().set("pom_customizePom", new Closure(project) {
            public Object call(Object... args) {
                assert args.length == 2;
                assert args[0] instanceof Project;
                assert args[1] instanceof MavenPublication;

                PomCustomizer.customizePom((Project) args[0], (MavenPublication) args[1]);

                return null;
            }
        });
    }
}

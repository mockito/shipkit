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

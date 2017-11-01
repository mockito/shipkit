package org.shipkit.internal.gradle.java;

import org.gradle.api.*;
import org.gradle.api.file.CopySpec;
import org.gradle.api.tasks.bundling.Jar;
import org.shipkit.internal.gradle.util.JavaPluginUtil;

import java.io.File;

/**
 * Makes a java library that has not only the main jar but also sources and javadoc jars.
 * Intended to be applied in individual Java submodule.
 * <p>
 * Applies and configures following plugins:
 *
 * <ul>
 *     <li>java</li>
 * </ul>
 *
 * Adds following tasks:
 * <ul>
 *     <li>sourcesJar</li>
 *     <li>javadocJar</li>
 * </ul>
 *
 * Adds following behavior:
 * <ul>
 *     <li>Adds tasks to create javadoc and sources jars</li>
 *     <li>Adds new jars to "archives" configuration</li>
 *     <li>Includes "LICENSE" file in jars</li>
 * </ul>
 */
public class JavaLibraryPlugin implements Plugin<Project> {

    public final static String SOURCES_JAR_TASK = "sourcesJar";
    public final static String JAVADOC_JAR_TASK = "javadocJar";

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply("java");

        final CopySpec license = project.copySpec(new Action<CopySpec>() {
            public void execute(CopySpec copy) {
                copy.from(project.getRootDir()).include("LICENSE");
            }
        });

        ((Jar) project.getTasks().getByName("jar")).with(license);

        final Jar sourcesJar = project.getTasks().create(SOURCES_JAR_TASK, Jar.class, new Action<Jar>() {
            public void execute(Jar jar) {
                jar.from(JavaPluginUtil.getMainSourceSet(project).getAllSource());
                jar.setClassifier("sources");
                jar.with(license);
            }
        });

        final Task javadocJar = project.getTasks().create(JAVADOC_JAR_TASK, Jar.class, new Action<Jar>() {
            public void execute(Jar jar) {
                jar.from(project.getTasks().getByName("javadoc"));
                jar.setClassifier("javadoc");
                jar.with(license);
            }
        });

        project.getArtifacts().add("archives", sourcesJar);
        project.getArtifacts().add("archives", javadocJar);
    }
}

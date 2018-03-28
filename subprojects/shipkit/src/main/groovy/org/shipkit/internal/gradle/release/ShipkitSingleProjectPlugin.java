package org.shipkit.internal.gradle.release;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.bintray.ShipkitBintrayPlugin;

/**
 * Continuous delivery for a basic project that wants to publish to Bintray with Travis.
 * Assumes that the root Gradle project builds the artifacts that you want to publish.
 * Intended for basic, single-project Gradle builds.
 * Originally created to enable Shipkit in project (<a href="https://github.com/linkedin/play-parseq">https://github.com/linkedin/play-parseq</a>).
 * <p>
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link ShipkitBasePlugin} - base building blocks for release automation</li>
 *     <li>{@link ShipkitBintrayPlugin} - so that the single root project will be released to Bintray</li>
 * </ul>
 */
public class ShipkitSingleProjectPlugin implements Plugin<Project> {

    public void apply(final Project project) {
        project.getPlugins().apply(ShipkitBasePlugin.class);
        project.getPlugins().apply(ShipkitBintrayPlugin.class);
    }
}

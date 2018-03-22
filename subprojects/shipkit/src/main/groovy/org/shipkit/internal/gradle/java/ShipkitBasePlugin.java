package org.shipkit.internal.gradle.java;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.bintray.BintrayReleasePlugin;
import org.shipkit.internal.gradle.release.TravisPlugin;

/**
 * Continuous delivery for any project that wants to publish to Bintray with Travis.
 * Intended for root project of your Gradle project.
 * Originally created to enable Shipkit in project (<a href="https://github.com/linkedin/play-parseq">https://github.com/linkedin/play-parseq</a>).
 * <p>
 * Applies following plugins:
 *
 * <ul>
 *     <li>{@link TravisPlugin}</li>
 *     <li>{@link BintrayReleasePlugin}</li>
 * </ul>
 */
public class ShipkitBasePlugin implements Plugin<Project> {

    public void apply(final Project project) {
        project.getPlugins().apply(TravisPlugin.class);
        project.getPlugins().apply(BintrayReleasePlugin.class);
    }
}

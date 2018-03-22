package org.shipkit.internal.gradle.release;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.bintray.BintrayReleasePlugin;
import org.shipkit.internal.gradle.bintray.ShipkitBintrayPlugin;

/**
 * Continuous delivery for any project that wants to publish to Bintray with Travis.
 * Intended for root project of your multi-project Gradle build.
 * In order for this plugin to work, please apply {@link ShipkitBintrayPlugin} to
 * every Gradle subproject that you want to publish to Bintray.
 *
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

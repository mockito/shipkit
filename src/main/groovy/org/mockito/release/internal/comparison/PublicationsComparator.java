package org.mockito.release.internal.comparison;

/**
 * Compares publications
 *
 * TODO WW, let's get rid of this interface.
 * We already assume in the code the implementation of this interface needs to be a task
 * because we pass the instance to the dependsOn() method.
 * Additionally, all task classes that we have we need to make public classes, part of public API.
 * See example BumpVersionFileTask, IncrementalReleaseNotes that are already public and well documented.
 * Keeping the interface is nice from the standpoint of decouping but it would make the whole api more complicated.
 * Since we make the task class public, we would have to make the interface public, too.
 * If we follow this trend we would have a proliferation of interfaces for many task classes.
 * I did try the approach of having interfaces on top of tasks in this project initially,
 * However, I gave up at some point :)
 * Let's ditch this interface and use PublicationsComparatorTask, which will be a part of public API.
 */
public interface PublicationsComparator {

    /**
     * Gives information if publications are equal
     */
    boolean isPublicationsEqual();
}

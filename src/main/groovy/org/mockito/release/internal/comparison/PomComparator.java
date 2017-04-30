package org.mockito.release.internal.comparison;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.util.Set;

import static org.mockito.release.internal.util.ArgumentValidation.notNull;

class PomComparator {

    private static final Logger log = Logging.getLogger(PomComparator.class);

    private final String left;
    private final String right;
    private final Set<BaseProjectProperties> dependentSiblingProjects;

    PomComparator(String left, String right, Set<BaseProjectProperties> dependentSiblingProjects) {
        notNull(left, "pom content to compare", right, "pom content to compare");
        this.left = left;
        this.right = right;
        this.dependentSiblingProjects = dependentSiblingProjects;
    }

    boolean areEqual() {
        PomSiblingDependencyRemover remover = new PomSiblingDependencyRemover();
        String parsedLeft = remover.removeSiblingDependencies(left, dependentSiblingProjects);
        String parsedRight = remover.removeSiblingDependencies(right, dependentSiblingProjects);
        return parsedLeft.equals(parsedRight);
    }
}

package org.mockito.release.internal.comparison;

import static org.mockito.release.internal.util.ArgumentValidation.notNull;

class PomComparator {

    private final String left;
    private final String right;

    PomComparator(String left, String right) {
        notNull(left, "pom content to compare", right, "pom content to compare");
        this.left = left;
        this.right = right;
    }

    boolean areEqual() {
        return replaceVersion(left).equals(replaceVersion(right));
    }

    private String replaceVersion(String pom) {
        return pom.replaceFirst("<version>(.*)</version>", "<version>foobar</version>");
    }
}

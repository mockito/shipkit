package org.mockito.release.internal.comparison;

import java.io.File;

interface FileComparator {
    boolean areEqual(File one, File other);
}

package org.shipkit.internal.comparison;

import java.io.File;

interface FileComparator {
    boolean areEqual(File one, File other);
}

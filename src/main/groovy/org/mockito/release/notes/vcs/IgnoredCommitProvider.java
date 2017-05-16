package org.mockito.release.notes.vcs;

import java.util.HashMap;

public class IgnoredCommitProvider {
    private static HashMap<String, Class<? extends IgnoredCommit>> ignoredCommitTypeToClassMap;

    public static IgnoredCommit fromType(String type) {
        final Class<? extends IgnoredCommit> classByType = getClassByType(type);
        try {
            return classByType.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unknown ignored commit type: " + type);
        }
    }

    private static Class<? extends IgnoredCommit> getClassByType(String type) {
        if (ignoredCommitTypeToClassMap == null) {
            ignoredCommitTypeToClassMap = new HashMap<String, Class<? extends IgnoredCommit>>();
            ignoredCommitTypeToClassMap.put(IgnoreCiSkip.TYPE, IgnoreCiSkip.class);
        }
        return ignoredCommitTypeToClassMap.get(type);
    }
}

package org.shipkit.internal.comparison.diff;

import java.util.List;

public class DirectoryDiffGenerator {

    public String generateDiffOutput(List<String> addedFiles, List<String> removedFiles, List<String> changedFiles) {
        StringBuilder sb = new StringBuilder();

        appendFiles(sb, addedFiles, "Added files", "++");
        appendFiles(sb, removedFiles, "Removed files", "--");
        appendFiles(sb, changedFiles, "Modified files", "+-");

        return sb.toString();
    }

    private void appendFiles(StringBuilder sb, List<String> changedFiles, String fileType, String symbol) {
        if (changedFiles != null && !changedFiles.isEmpty()) {
            String header = String.format("    %s:\n", fileType);
            sb.append(header);
            for (String changed : changedFiles) {
                sb.append(String.format("    %s %s\n", symbol, changed));
            }
            sb.append("\n");
        }
    }
}

package org.shipkit.internal.version;

import org.shipkit.internal.gradle.util.StringUtil;
import org.shipkit.internal.notes.util.IOUtil;
import org.shipkit.internal.util.PropertiesUtil;
import org.shipkit.version.VersionInfo;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

class DefaultVersionInfo implements VersionInfo {

    private final File versionFile;
    private final LinkedList<String> notableVersions;
    private final String version;
    private final String previousVersion;

    DefaultVersionInfo(File versionFile, String version, LinkedList<String> notableVersions, String previousVersion) {
        this.versionFile = versionFile;
        this.version = version;
        this.notableVersions = notableVersions;
        this.previousVersion = previousVersion;
    }

    static DefaultVersionInfo fromFile(File versionFile) {
        Properties properties = PropertiesUtil.readProperties(versionFile);
        String version = properties.getProperty("version");
        if (version == null) {
            throw new IllegalArgumentException("Missing 'version=' properties in file: " + versionFile);
        }
        String previousVersion = properties.getProperty("previousVersion");
        LinkedList<String> notableVersions = parseNotableVersions(properties);
        return new DefaultVersionInfo(versionFile, version, notableVersions, previousVersion);
    }

    private static LinkedList<String> parseNotableVersions(Properties properties) {
        LinkedList<String> result = new LinkedList<>();
        String value = properties.getProperty("notableVersions");
        if (value != null) {
            String[] versions = value.split(",");
            for (String v : versions) {
                result.add(v.trim());
            }
        }
        return result;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getPreviousVersion() {
        return previousVersion;
    }

    public DefaultVersionInfo bumpVersion() {
        return bumpVersion(false);
    }

    public DefaultVersionInfo bumpVersion(boolean updateNotable) {
        String content = IOUtil.readFully(versionFile);
        if (updateNotable) {
            notableVersions.addFirst(version);
            String asString = "notableVersions=" + StringUtil.join(notableVersions, ", ") + "\n";
            if (notableVersions.size() == 1) {
                //when no prior notable versions, we just add new entry
                content += "\n" + asString;
            } else {
                //update existing entry
                content = content.replaceAll("(?m)^notableVersions=(.*?)\n", asString);
            }
        }

        String previousVersion = this.version;
        String newVersion = new VersionBumper().incrementVersion(this.version);
        if (!content.endsWith("\n")) {
            //This makes the regex simpler. Add arbitrary end of line at the end of file should not bother anyone.
            //See also unit tests for this class
            content += "\n";
        }
        String updated = content
                .replaceAll("(?m)^version=(.*?)\n", "version=" + newVersion + "\n")
                .replaceAll("(?m)^previousVersion=(.*?)\n", "previousVersion=" + previousVersion + "\n");

        if (!updated.contains("previousVersion")) {
            updated += "previousVersion=" + previousVersion + "\n";
        }

        IOUtil.writeFile(versionFile, updated);
        return new DefaultVersionInfo(versionFile, newVersion, notableVersions, previousVersion);
    }

    public Collection<String> getNotableVersions() {
        //TODO if by the end of Q3 (9/30/17) we don't find the concept of 'notable versions' useful
        //we should delete 'notableVersions' code from this class
        return notableVersions;
    }
}

package org.mockito.release.version;

import org.mockito.release.internal.gradle.util.StringUtil;
import org.mockito.release.notes.util.IOUtil;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

class DefaultVersionFile implements VersionFile {

    private final File versionFile;
    private final List<String> notableVersions;
    private String version;

    DefaultVersionFile(File versionFile) {
        this.versionFile = versionFile;
        Properties properties = readProperties(versionFile);
        this.version = properties.getProperty("version");
        if (version == null) {
            throw new IllegalArgumentException("Missing 'version=' properties in file: " + versionFile);
        }
        this.notableVersions = parseNotableVersions(properties);
    }

    private List<String> parseNotableVersions(Properties properties) {
        List<String> result = new LinkedList<String>();
        String value = properties.getProperty("notableVersions");
        if (value != null) {
            String[] versions = value.split(",");
            for (String v : versions) {
                result.add(v.trim());
            }
        }
        return result;
    }

    private static Properties readProperties(File versionFile) {
        Properties p = new Properties();
        FileReader reader = null;
        try {
            reader = new FileReader(versionFile);
            p.load(reader);
        } catch (Exception e) {
            throw new RuntimeException("Problems reading version file: " + versionFile);
        } finally {
            IOUtil.close(reader);
        }
        return p;
    }

    public String getVersion() {
        return version;
    }

    public String bumpVersion(boolean updateNotable) {
        String content = IOUtil.readFully(versionFile);
        if (updateNotable) {
            notableVersions.add(version);
            String asString = "notableVersions=" + StringUtil.join(notableVersions, ", ") + "\n";
            if (notableVersions.size() == 1) {
                //when no prior notable versions, we just add new entry
                content += "\n" + asString;
            } else
                //update existing entry
                content = content.replaceAll("(?m)^notableVersions=(.*?)\n", asString);
        }

        VersionBumper bumper = new VersionBumper();
        version = bumper.incrementVersion(this.version);
        if (!content.endsWith("\n")) {
            //This makes the regex simpler. Add arbitrary end of line at the end of file should not bother anyone.
            //See also unit tests for this class
            content += "\n";
        }
        String updated = content.replaceAll("(?m)^version=(.*?)\n", "version=" + version + "\n");

        IOUtil.writeFile(versionFile, updated);
        return version;
    }

    public Collection<String> getNotableVersions() {
        return notableVersions;
    }
}

package org.shipkit.internal.notes.vcs;

import org.shipkit.internal.exec.ProcessRunner;
import org.shipkit.internal.util.DateUtil;

import java.util.*;

class DefaultReleasedVersionsProvider implements ReleasedVersionsProvider {

    private final RevisionDateProvider dateProvider;

    DefaultReleasedVersionsProvider(ProcessRunner runner) {
        this(new RevisionDateProvider(runner));
    }

    DefaultReleasedVersionsProvider(RevisionDateProvider dateProvider) {
        this.dateProvider = dateProvider;
    }

    @Override
    public Collection<ReleasedVersion> getReleasedVersions(String headVersion, Date headDate, Collection<String> versions, String tagPrefix) throws RevisionNotFoundException {
        //collect the versions
        if (versions.size() == 0 && headVersion == null) {
            throw new IllegalArgumentException("Not enough versions supplied." +
                    "\n  I need at least 1 version." +
                    "\n   - head version: " + headVersion +
                    "\n   - versions: " + versions);
        }
        if (headVersion != null && headDate == null) {
            throw new IllegalArgumentException("headDate cannot be null if headVersion is provided");
        }
        List<String> theVersions = new ArrayList<String>(versions);
        LinkedList<ReleasedVersion> result = new LinkedList<ReleasedVersion>();

        for (int i = 0; i < theVersions.size(); i++) {
            String v = theVersions.get(i);
            String tag = tagPrefix + v;
            //the value of 'next' element in collection is the 'previous version' because the input versions are sorted descending
            String previous = (theVersions.size() > (i + 1)) ? tagPrefix + theVersions.get(i + 1) : null;
            Date date = dateProvider.getDate(tag);
            result.add(new DefaultReleasedVersion(v, date, tag, previous));
        }

        if (headVersion != null) {
            String prev = result.isEmpty() ? null : result.get(0).getRev();
            DefaultReleasedVersion head = new DefaultReleasedVersion(headVersion, headDate, "HEAD", prev);
            result.addFirst(head);
        }
        return result;
    }

    private static class DefaultReleasedVersion implements ReleasedVersion {
        private final String version;
        private final Date date;
        private final String rev;
        private final String previousRev;

        DefaultReleasedVersion(String version, Date date, String rev, String previousRev) {
            this.version = version;
            this.date = date;
            this.rev = rev;
            this.previousRev = previousRev;
        }

        public String getVersion() {
            return version;
        }

        public Date getDate() {
            return date;
        }

        public String getRev() {
            return rev;
        }

        public String getPreviousRev() {
            return previousRev;
        }

        public String toString() {
            return "" + version + "@" + (date != null ? DateUtil.formatDate(date) : "<no date>") + "(" + rev + ".." + previousRev + ")";
        }
    }
}

package org.mockito.release.notes.vcs;

import org.mockito.release.exec.ProcessRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class DefaultReleaseDateProvider implements ReleaseDateProvider {

    private final ProcessRunner runner;

    DefaultReleaseDateProvider(ProcessRunner runner) {
        this.runner = runner;
    }

    @Override
    public Map<String, Date> getReleaseDates(Iterable<String> versions, String tagPrefix) {
        //TODO SF use this to get version of a tag:
        //git log --simplify-by-decoration --pretty="format:%ai %d" v2.6.1..v2.7.0
        //git log --simplify-by-decoration --pretty="format:%ai %d" <tag prefix + last element of versions>..<tag prefix + first element of versions>
        //example result from mockito:
        /*
~/mockito/src$ git log --simplify-by-decoration --pretty="format:%ai %d" v2.6.1..v2.7.0
2017-01-29 08:14:09 -0800  (tag: v2.7.0)
2017-01-23 21:28:50 -0800  (origin/strict-stubbing, strict-stubbing)
2017-01-27 22:02:58 +0000  (tag: v2.6.9)
2017-01-23 14:53:02 +0000  (tag: v2.6.8)
2017-01-23 13:52:08 +0000  (tag: v2.6.7)
2017-01-23 10:12:22 +0000  (tag: v2.6.6)
2017-01-23 11:07:23 +0100
2017-01-20 14:50:39 +0100  (origin/fix-878-spy-annotation-abstract-class)
2017-01-21 12:58:32 +0000  (tag: v2.6.5)
2017-01-19 17:12:14 +0000  (tag: v2.6.4)
2017-01-15 21:07:48 +0000  (tag: v2.6.3)
2017-01-13 10:58:06 +0000  (tag: v2.6.2)
         */

        Map<String, Date> out = new HashMap<String, Date>();
        for (String version : versions) {
            out.put(version, new Date());
        }
        return out;
    }
}

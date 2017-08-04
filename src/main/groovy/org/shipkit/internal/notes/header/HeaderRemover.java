package org.shipkit.internal.notes.header;

public class HeaderRemover {

    public static String removeHeaderIfExist(String existing) {
        return existing.replaceFirst(HeaderProvider.HEADER_START + ".*" + HeaderProvider.HEADER_END + "[\\r\\n]+", "");
    }
}

package org.shipkit.internal.notes.header;

public class HeaderProvider {

    public static final String HEADER_START = "<sup><sup>";
    public static final String HEADER_END = "</sup></sup>";

    public String getHeader(String header) {
        return HEADER_START + "*" + header + "*" + HEADER_END + "\n\n";
    }
}

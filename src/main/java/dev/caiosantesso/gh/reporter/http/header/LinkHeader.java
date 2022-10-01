package dev.caiosantesso.gh.reporter.http.header;

import java.util.regex.Pattern;

public class LinkHeader {
    private LinkHeader(){}

    public enum Rel {
        FIRST, PREVIOUS, NEXT, LAST
    }
    public static String parse(String linkHeader, Rel rel) {
        return Pattern
                .compile(",")
                .splitAsStream(linkHeader)
                .filter(link -> link.contains(rel.name().toLowerCase()))
                .map(link -> link
                        .replaceFirst(".*<", "")
                        .replaceFirst(">.*", ""))
                .findAny()
                .orElseThrow();
    }
}

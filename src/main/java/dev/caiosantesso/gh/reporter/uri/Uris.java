package dev.caiosantesso.gh.reporter.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.stream.IntStream;

import static java.lang.Integer.*;
import static java.util.stream.Collectors.toSet;

public class Uris {
    private Uris() {}

    public static Collection<URI> generateFrom(URI lastUri) {
        var query = lastUri.getQuery();
        var lastPageNumber = parseInt(query.substring(1 + query.lastIndexOf("=")));

        return IntStream
                .rangeClosed(1, lastPageNumber)
                .mapToObj(pageNumber -> uriForPage(lastUri, pageNumber))
                .collect(toSet());
    }

    private static URI uriForPage(URI lastUri, int pageNumber) {
        String query = "page=%d".formatted(pageNumber);

        var lastUriQuery = lastUri.getQuery();
        if (lastUriQuery != null) {
            var queryWithoutPage = lastUriQuery
                    .substring(0, lastUriQuery
                            .lastIndexOf("page"));
            query = queryWithoutPage + query;
        }

        try {
            return new URI(lastUri.getScheme(), lastUri.getHost(), lastUri.getPath(), query, null);
        } catch (URISyntaxException e) {
            throw new URIException(e);
        }
    }

    private static class URIException extends RuntimeException {

        public URIException(URISyntaxException e) {
            super(e);
        }
    }
}

package dev.caiosantesso.gh.reporter.http;

import dev.caiosantesso.gh.reporter.http.header.Header;
import dev.caiosantesso.gh.reporter.http.header.LinkHeader;
import dev.caiosantesso.gh.reporter.uri.Uris;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import static java.net.http.HttpResponse.BodyHandlers.discarding;
import static java.net.http.HttpResponse.BodyHandlers.ofInputStream;
import static java.util.Objects.requireNonNull;

public class RequestBuilder {
    private final HttpClient client;
    private final String token;
    private static final String AUTH_HEADER = "Authorization";

    public RequestBuilder(HttpClient client, String token) {
        this.client = requireNonNull(client);
        this.token = requireNonNull(token);
    }

    public Collection<URI> pagesToRequest(String seedUri) {
        return pagesToRequest(seedUri, null);
    }

    public Collection<URI> pagesToRequest(String seedUri, Header additionalHeader) {
        var res = requestNumberOfPages(seedUri, additionalHeader);
        var linkHeader = res
                .headers()
                .firstValue("link");

        if (linkHeader.isPresent()) {
            var lastUri = LinkHeader.parse(linkHeader.get(), LinkHeader.Rel.LAST);
            return Uris.generateFrom(URI.create(lastUri));
        }
        return Set.of(URI.create(seedUri));

    }

    private HttpResponse<Void> requestNumberOfPages(String seedUri, Header additionalHeader) {
        HttpRequest.Builder builder = HttpRequest
                .newBuilder(URI.create(seedUri))
                .header(AUTH_HEADER, token);

        if (additionalHeader != null) builder.header(additionalHeader.key(), additionalHeader.value());
        var request = builder.build();

        try {
            return client.send(request, discarding());
        } catch (IOException | InterruptedException e) {
            throw new HttpException(e);
        }
    }

    public <T> Collection<T> requestAll(Collection<URI> uris, Function<InputStream, List<T>> typedParser) {
        var repos = new CopyOnWriteArrayList<T>();

        CompletableFuture
                .allOf(uris
                        .stream()
                        .map(HttpRequest::newBuilder)
                        .map(req -> req.header(AUTH_HEADER, token))
                        .map(HttpRequest.Builder::build)
                        .map(request -> client
                                .sendAsync(request, ofInputStream())
                                .thenApply(this::validateResponse)
                                .thenApply(typedParser)
                                .thenAccept(repos::addAll))
                        .toArray(CompletableFuture<?>[]::new))
                .join();

        return repos;
    }

    public <T> Collection<T> requestForTextMatch(Collection<URI> uris, Function<InputStream, T> typedParser) {
        var repos = new CopyOnWriteArrayList<T>();

        CompletableFuture
                .allOf(uris
                        .stream()
                        .map(HttpRequest::newBuilder)
                        .map(req -> req.header(AUTH_HEADER, token))
                        .map(req -> req.header("Accept", "application/vnd.github.text-match+json"))
                        .map(HttpRequest.Builder::build)
                        .map(request -> client
                                .sendAsync(request, ofInputStream())
                                .thenApply(this::validateResponse)
                                .thenApply(typedParser)
                                .thenApply(repos::add))
                        .toArray(CompletableFuture<?>[]::new))
                .join();

        return repos;
    }

    private InputStream validateResponse(HttpResponse<InputStream> response) {
        var body = response.body();
        if (response.statusCode() == 200) return body;
        try {
            throw new HttpException(new String(body.readAllBytes()));
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }
}

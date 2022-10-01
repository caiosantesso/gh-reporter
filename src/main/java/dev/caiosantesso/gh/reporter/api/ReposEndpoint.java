package dev.caiosantesso.gh.reporter.api;

import dev.caiosantesso.gh.reporter.http.RequestBuilder;
import dev.caiosantesso.gh.reporter.parser.Json;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.net.http.HttpClient;
import java.util.Collection;

public class ReposEndpoint {

    private final String url;
    private final RequestBuilder requests;

    public record Repo(@JsonAlias("full_name") String fullName, String name) {}

    public ReposEndpoint(HttpClient client, String token, String org) {
        this.requests = new RequestBuilder(client, token);
        this.url = "https://api.github.com/orgs/%s/repos".formatted(org);
    }

    public Collection<Repo> fetchAll() {
        var uris = requests.pagesToRequest(url);
        if (uris.isEmpty()) throw new IllegalArgumentException("At least one URI is required");
        return requests.requestAll(uris, Json::parseRepo);
    }
}

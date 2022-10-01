package dev.caiosantesso.gh.reporter.api;

import dev.caiosantesso.gh.reporter.api.ReposEndpoint.Repo;
import dev.caiosantesso.gh.reporter.http.RequestBuilder;
import dev.caiosantesso.gh.reporter.parser.Json;

import java.net.http.HttpClient;
import java.util.Collection;

public class BranchesEndpoint {
    private static final String URL = "https://api.github.com/repos/%s/branches";
    private final RequestBuilder requests;

    public record Branch(String name) {}

    public BranchesEndpoint(HttpClient client, String token) {
        this.requests = new RequestBuilder(client, token);
    }

    public Collection<Branch> fetchAllFor(Repo repo) {
        var uris = requests.pagesToRequest(URL.formatted(repo.fullName()));
        if (uris.isEmpty()) throw new IllegalArgumentException("At least one URI is required");
        return requests.requestAll(uris, Json::parseBranches);
    }

}

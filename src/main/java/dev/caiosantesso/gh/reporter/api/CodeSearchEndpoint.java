package dev.caiosantesso.gh.reporter.api;

import com.fasterxml.jackson.annotation.JsonAlias;
import dev.caiosantesso.gh.reporter.api.ReposEndpoint.Repo;
import dev.caiosantesso.gh.reporter.http.RequestBuilder;
import dev.caiosantesso.gh.reporter.http.header.Header;
import dev.caiosantesso.gh.reporter.parser.Json;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

public class CodeSearchEndpoint {
    private final RequestBuilder requests;
    private final String org;
    private static final String urlTemplate = "https://api.github.com/search/code?q=%s";

    public record TextMatch(String fragment, String property) {}

    public record Item(@JsonAlias("text_matches") List<TextMatch> textMatches, Repo repository) {}

    public record SearchResult(List<Item> items) {}


    public CodeSearchEndpoint(HttpClient client, String token, String org) {
        this.org = org;
        this.requests = new RequestBuilder(client, token);
    }

    public Collection<SearchResult> searchOnPackageDotJson(String term) {
        String uri = composeUri(term);
        var uris = requests.pagesToRequest(uri, new Header("Accept", "application/vnd.github.text-match+json"));
        if (uris.isEmpty()) throw new IllegalArgumentException("At least one URI is required");
        return requests.request(uris, Json::parseTextMatches);
    }

    private String composeUri(String term) {
        var query = String.join(" ", term, "org:" + org, "filename:package.json", "in:file");
        var queryEncoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        return urlTemplate.formatted(queryEncoded);
    }
}

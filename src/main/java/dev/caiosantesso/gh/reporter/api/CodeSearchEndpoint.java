package dev.caiosantesso.gh.reporter.api;

import com.fasterxml.jackson.annotation.JsonAlias;
import dev.caiosantesso.gh.reporter.api.ReposEndpoint.Repo;
import dev.caiosantesso.gh.reporter.http.RequestService;
import dev.caiosantesso.gh.reporter.http.header.Header;
import dev.caiosantesso.gh.reporter.parser.Json;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

public class CodeSearchEndpoint {
    private final RequestService requests;
    private final String org;

    public record TextMatch(String fragment, String property) {}

    public record Item(@JsonAlias("text_matches") List<TextMatch> textMatches, Repo repository) {}

    public record SearchResult(List<Item> items) {}

    public CodeSearchEndpoint(HttpClient client, String token, String org) {
        this.org = org;
        this.requests = new RequestService(client, token);
    }

    public Collection<SearchResult> searchInFile(String content, String filename) {
        var uri = composeUri(content, filename);
        var uris = requests.pagesToRequest(uri, new Header("Accept", "application/vnd.github.text-match+json"));
        if (uris.isEmpty()) throw new IllegalArgumentException("At least one URI is required");
        return requests.requestForTextMatch(uris, Json::parseTextMatches);
    }

    private String composeUri(String content, String filename) {
        var query = String.join(" ", content, "org:" + org, "in:file");
        if (filename != null) query += " filename:%s".formatted(filename);

        var queryEncoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        return "https://api.github.com/search/code?q=%s".formatted(queryEncoded);
    }
}

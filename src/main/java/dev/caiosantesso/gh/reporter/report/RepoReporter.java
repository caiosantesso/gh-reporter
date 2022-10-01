package dev.caiosantesso.gh.reporter.report;

import dev.caiosantesso.gh.reporter.api.BranchesEndpoint;
import dev.caiosantesso.gh.reporter.api.CodeSearchEndpoint;
import dev.caiosantesso.gh.reporter.api.CodeSearchEndpoint.Item;
import dev.caiosantesso.gh.reporter.api.ReposEndpoint;
import dev.caiosantesso.gh.reporter.api.ReposEndpoint.Repo;
import dev.caiosantesso.gh.reporter.file.Csv;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static dev.caiosantesso.gh.reporter.api.CodeSearchEndpoint.SearchResult;
import static java.util.Objects.requireNonNull;

public class RepoReporter {
    private final HttpClient client;
    private final String token;
    private final String org;

    private static final Logger logger = Logger.getLogger(RepoReporter.class.getName());

    public RepoReporter(String token, String org) {
        this.client = requireNonNull(HttpClient.newHttpClient());
        this.token = requireNonNull("Bearer %s".formatted(token));
        this.org = requireNonNull(org);
    }

    public void saveAllBranchesToCsv() {
        save(this::requestAllBranches, "branches");
    }

    public void saveWithMatchingContentToCsv(String term) {
        requireNonNull(term);

        save(() -> searchPackageDotJsonInReposFor(term), "in_package_json");
    }

    private void save(Supplier<List<String>> csvRowsSupplier, String dir) {
        var start = LocalDateTime.now();
        var csvRows = csvRowsSupplier.get();
        Csv.save(csvRows, dir);
        printFooter(start, csvRows.size());
    }

    private List<String> requestAllBranches() {
        var repoEndpoint = new ReposEndpoint(client, token, org);
        var repos = repoEndpoint.fetchAll();

        var branchEndpoint = new BranchesEndpoint(client, token);

        return repos
                .stream()
                .parallel()
                .flatMap(repo -> branchEndpoint
                        .fetchAllFor(repo)
                        .stream()
                        .map(branch -> "%s,%s".formatted(repo.name(), branch.name())))
                .toList();
    }

    private List<String> searchPackageDotJsonInReposFor(String term) {
        var codeSearch = new CodeSearchEndpoint(client, token, org);
        var searchResults = codeSearch.searchOnPackageDotJson(term);
        return searchResults
                .stream()
                .map(SearchResult::items)
                .flatMap(Collection::stream)
                .map(Item::repository)
                .map(Repo::name)
                .distinct()
                .toList();
    }

    private void printFooter(LocalDateTime start, int rows) {
        logger.info(() -> "Rows: %d".formatted(rows));
        logger.info(() -> "Time elapsed(ms): %d".formatted(Duration
                .between(start, LocalDateTime.now())
                .toMillis()));
    }
}

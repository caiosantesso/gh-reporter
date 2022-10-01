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
import java.util.stream.Stream;

import static dev.caiosantesso.gh.reporter.api.CodeSearchEndpoint.SearchResult;
import static java.util.Objects.requireNonNull;

public class RepoReporter {
    private final HttpClient client;
    private final String token;
    private final String org;

    private static final Logger logger = Logger.getLogger(RepoReporter.class.getName());

    private record RepoBranches(Repo repo, Collection<BranchesEndpoint.Branch> branches) {}

    public RepoReporter(String token, String org) {
        this.client = requireNonNull(HttpClient.newHttpClient());
        this.token = requireNonNull("Bearer %s".formatted(token));
        this.org = requireNonNull(org);
    }

    public void saveAllBranchesToCsv() {
        save(() -> {
            var branches = requestAllBranches();
            return toCsvRows(branches.stream());
        }, "branches");
    }

    public void saveMatchingBranchesToCsv(String branchName) {
        requireNonNull(branchName);

        save(() -> {
            var reposWithBranches = requestAllBranches();
            return filterByBranch(reposWithBranches.stream(), branchName);
        }, "branches/%s/".formatted(branchName));
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

    private List<RepoBranches> requestAllBranches() {
        var repoEndpoint = new ReposEndpoint(client, token, org);
        var repos = repoEndpoint.fetchAll();

        var branchEndpoint = new BranchesEndpoint(client, token);

        return repos
                .stream()
                .parallel()
                .map(repo -> {
                    var branches = branchEndpoint.fetchAllFor(repo);
                    return new RepoBranches(repo, branches);
                })
                .toList();
    }

    private List<String> filterByBranch(Stream<RepoBranches> reposWithBranches, String branchName) {
        return reposWithBranches
                .filter(r -> r.branches
                        .stream()
                        .anyMatch(branch -> branchName.equals(branch.name())))
                .map(r -> r.repo.name())
                .sorted()
                .toList();
    }

    private List<String> toCsvRows(Stream<RepoBranches> branches) {
        return branches
                .flatMap(repo -> repo
                        .branches()
                        .stream()
                        .map(branch -> "%s,%s".formatted(repo
                                .repo()
                                .name(), branch.name())))
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

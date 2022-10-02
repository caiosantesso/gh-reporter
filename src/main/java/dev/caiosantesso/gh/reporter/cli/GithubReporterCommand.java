package dev.caiosantesso.gh.reporter.cli;

import dev.caiosantesso.gh.reporter.report.RepoReporter;
import picocli.CommandLine.*;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Command(name = "ghreporter",
         subcommands = {HelpCommand.class},
         description = "List GitHub repos",
         mixinStandardHelpOptions = true)
public final class GithubReporterCommand implements Runnable {

    @Mixin
    private HelpUsage helpUsage;
    @Option(names = {"--org"},
            required = true,
            description = "GitHub org")
    private String org;

    @ArgGroup(exclusive = false)
    private SearchOptions searchOptions;

    static class SearchOptions {
        @Option(names = {"--content"},
                required = true,
                description = "Search all repos' files with this content")
        private String content;

        @Option(names = {"--filename"},
                description = "Search only in this file")
        private String filename;
    }

    @Override
    public void run() {
        var token = requireNonNull(System.getenv("GITHUB_TOKEN"), "You must set the GH PAT!");
        var reposReporter = new RepoReporter(token, org);

        if (isNull(searchOptions)) reposReporter.saveAllBranchesToCsv();
        else reposReporter.saveWithMatchingContentToCsv(searchOptions.content, searchOptions.filename);
    }
}

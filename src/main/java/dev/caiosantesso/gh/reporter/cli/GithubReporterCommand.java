package dev.caiosantesso.gh.reporter.cli;

import dev.caiosantesso.gh.reporter.report.RepoReporter;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Command(name = "ghreporter",
        subcommands = {HelpCommand.class},
        description = "List GitHub repos",
        mixinStandardHelpOptions = true)
public final class GithubReporterCommand implements Runnable {
    @Option(names = {"--org"}, required = true, description = "GitHub org")
    private String org;

    @Option(names = {"--content"}, description = "All repos whose default branch contains files with this content")
    private String content;

    @Override
    public void run() {
        var token = requireNonNull(System.getenv("GITHUB_TOKEN"), "You must set the GH PAT!");
        var reposReporter = new RepoReporter(token, org);

        if (isNull(content))
            reposReporter.saveAllBranchesToCsv();
        else
            reposReporter.saveWithMatchingContentToCsv(content);
    }
}

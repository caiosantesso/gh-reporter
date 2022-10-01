package dev.caiosantesso.gh.reporter;

import dev.caiosantesso.gh.reporter.cli.GithubReporterCommand;
import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GithubReporterCommand()).execute(args);
        System.exit(exitCode);
    }
}

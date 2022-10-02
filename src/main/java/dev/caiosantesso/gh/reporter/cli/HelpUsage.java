package dev.caiosantesso.gh.reporter.cli;

import picocli.CommandLine.*;

@Command(version = {"@|fg(magenta) ghreporter 2022.10.alpha|@",
        "Picocli " + picocli.CommandLine.VERSION,
        "JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
        "OS: ${os.name} ${os.version} ${os.arch}"},
         optionListHeading = "%nOptions:%n",
         commandListHeading = "%nCommands:%n",
         descriptionHeading = "%n")
public class HelpUsage {}

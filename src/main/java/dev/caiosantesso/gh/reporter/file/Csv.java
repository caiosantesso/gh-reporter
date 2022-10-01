package dev.caiosantesso.gh.reporter.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class Csv {
    private static final Logger logger = Logger.getLogger(Csv.class.getName());

    private Csv() {}

    public static void save(Iterable<String> rows, String dir) {
        var report = Path.of("reports/%s/%s.csv".formatted(dir, LocalDateTime.now()));
        var parent = report.getParent();
        try {
            Files.createDirectories(parent);
            Files.write(report, rows, CREATE_NEW);
            logger.info(() -> "Saved at %s".formatted(report));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

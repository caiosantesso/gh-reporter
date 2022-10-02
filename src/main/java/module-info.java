module gh.reporter {
    requires java.base;
    requires java.logging;
    requires java.net.http;
    requires info.picocli;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens dev.caiosantesso.gh.reporter.cli to info.picocli;
}

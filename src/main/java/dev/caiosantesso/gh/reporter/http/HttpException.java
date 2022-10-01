package dev.caiosantesso.gh.reporter.http;

public class HttpException extends RuntimeException {
    public HttpException(Exception e) {
        super(e);
    }

    public HttpException(String msg) {
        super(msg);
    }
}

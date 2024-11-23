package com.github.lernejo.korekto.grader.api.http;

public class HttpConnectionException extends RuntimeException {
    public HttpConnectionException(Exception cause) {
        super(cause);
    }
}

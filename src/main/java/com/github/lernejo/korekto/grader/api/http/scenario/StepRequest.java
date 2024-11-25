package com.github.lernejo.korekto.grader.api.http.scenario;

import com.github.lernejo.korekto.grader.api.http.Http;
import com.github.lernejo.korekto.grader.api.http.Response;

import java.util.Map;

record StepRequest(String uri, Verb verb, Map<String, String> headers, String body, String dataSetName, String index) {

    Response executeWith(Http http) {
        return switch (verb) {
            case GET -> http.get(uri, headers);
            case POST -> http.post(uri, headers, body);
        };
    }
}

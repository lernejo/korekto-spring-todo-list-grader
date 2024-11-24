package com.github.lernejo.korekto.grader.api.http;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public record Response(int status, Map<String, String> headers, String body) {
    public static Response from(HttpResponse<String> response) {
        Map<String, String> headers = response.headers()
            .map()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(e -> e.getKey().toLowerCase(Locale.ROOT), e -> e.getValue().getFirst().toLowerCase(Locale.ROOT)));
        return new Response(response.statusCode(), headers, response.body());
    }

    public Result<JsonNode, JacksonException> jsonNode() {
        try {
            return Result.ok(Http.OM.readTree(body));
        } catch (JacksonException e) {
            return Result.err(e);
        }
    }
}

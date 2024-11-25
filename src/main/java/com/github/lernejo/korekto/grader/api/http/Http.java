package com.github.lernejo.korekto.grader.api.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lernejo.korekto.grader.api.TestData;
import com.github.lernejo.korekto.grader.api.http.scenario.ScenarioBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class Http implements AutoCloseable {

    public static final ObjectMapper OM = new ObjectMapper();
    private final HttpClient httpClient;
    private final String baseUrl;

    public Http(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder().build();
    }

    public static String basic(String username, String password) {
        return "basic " + Base64.getEncoder().encodeToString((username + ':' + password).getBytes(StandardCharsets.UTF_8));
    }

    public static boolean jsonCompare(JsonNode a, JsonNode b) {
        return a.equals(b);
    }

    public static JsonNode readTree(String content) {
        try {
            return OM.readTree(content);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String writeValueAsString(Object object) {
        try {
            return OM.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        httpClient.close();
    }

    public Response get(String path) {
        return get(path, Map.of());
    }

    public Response get(String path, Map<String, String> headers) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri(path))
                .header("Accept", "application/json");
            headers.forEach(requestBuilder::header);
            HttpResponse<String> response = httpClient.send(requestBuilder
                .build(), HttpResponse.BodyHandlers.ofString());
            return Response.from(response);
        } catch (IOException | InterruptedException e) {
            throw new HttpConnectionException(e);
        }
    }

    public Response post(String path, Map<String, String> headers, Object body) {
        try {
            return post(path, headers, OM.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Response post(String path, Map<String, String> headers, String json) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(uri(path))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
            headers.forEach(requestBuilder::setHeader);
            HttpResponse<String> response = httpClient.send(requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build(), HttpResponse.BodyHandlers.ofString());
            return Response.from(response);
        } catch (IOException | InterruptedException e) {
            throw new HttpConnectionException(e);
        }
    }

    @NotNull
    private URI uri(String path) {
        try {
            return new URI(baseUrl + path);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public ScenarioBuilder new_scenario() {
        return new ScenarioBuilder(this);
    }
}

package com.github.lernejo.korekto.grader.api.http.scenario;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.github.lernejo.korekto.grader.api.http.Http;
import com.github.lernejo.korekto.grader.api.http.Response;
import com.github.lernejo.korekto.grader.api.http.Result;

import java.util.*;
import java.util.stream.Collectors;

public class StepResponseMatcher {
    private static final ComparatorWithoutOrder NODE_COMPARATOR = new ComparatorWithoutOrder(true);
    private final int expectedStatusCode;
    private String expectedContentType;
    private List<String> expectedJsonNodeKeys;
    private Map<String, String> expectedJsonNodes;

    public StepResponseMatcher(int expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    public StepResponseMatcher and_json_body() {
        this.expectedContentType = "application/json";
        return this;
    }

    public StepResponseMatcher and_json_body_containing_node_keys(String... nodeKeys) {
        and_json_body();
        expectedJsonNodeKeys = Arrays.stream(nodeKeys).toList();
        return this;
    }

    public StepResponseMatcher and_json_body_containing_nodes(Map<String, String> expectedJsonNodes) {
        this.expectedJsonNodes = expectedJsonNodes;
        return this;
    }

    List<String> match(StepRequest request, Response response) {
        String authenticationHint = request.headers().containsKey("Authorization") ? "with" : "without";
        String datasetHint = request.dataSetName() != null ? "and dataset %s ".formatted(request.dataSetName()) : "";
        String errorPrefix = "Step %s - Expecting call to `%s %s` %s authentication %sto".formatted(request.index(), request.verb(), request.uri(), authenticationHint, datasetHint);
        if (expectedStatusCode != response.status()) {
            return List.of("%s return a %s status code, but was: %s"
                .formatted(errorPrefix, expectedStatusCode, response.status()));
        }
        if (expectedContentType != null) {
            String contentType = response.headers().get("content-type");
            if (contentType == null) {
                return List.of("%s have a header: Content-Type (with value: %s)".formatted(errorPrefix, expectedContentType));
            } else if (!contentType.startsWith(expectedContentType)) {
                return List.of("%s have header Content-Type with value: %s, but was: %s".formatted(errorPrefix, expectedContentType, contentType));
            }
        }
        if (expectedJsonNodeKeys != null) {
            Result<JsonNode, JacksonException> jsonParsing = response.jsonNode();
            if (jsonParsing.isErr()) {
                return List.of("%s have a JSON response body, but: %s".formatted(errorPrefix, jsonParsing.err()));
            }
            JsonNode root = jsonParsing.ok();
            String missingNodes = expectedJsonNodeKeys.stream()
                .map(e -> new Pair<>(e, root.get(e)))
                .map(p -> p.second() == null ? p.first() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
            if (!missingNodes.isEmpty()) {
                return List.of("%s have a response body with the following JSON nodes: %s".formatted(errorPrefix, missingNodes));
            }
        }
        if (expectedJsonNodes != null) {
            Result<JsonNode, JacksonException> jsonParsing = response.jsonNode();
            if (jsonParsing.isErr()) {
                return List.of("%s have a JSON response body, but: %s".formatted(errorPrefix, jsonParsing.err()));
            }
            JsonNode root = jsonParsing.ok();
            List<String> errors = new ArrayList<>();
            for (Map.Entry<String, String> expectedNode : expectedJsonNodes.entrySet()) {
                JsonNode jsonNode = root.get(expectedNode.getKey());
                if (jsonNode == null) {
                    errors.add("%s have a response body with the JSON node: %s".formatted(errorPrefix, expectedNode.getKey()));
                } else {
                    boolean textFailure = jsonNode.getNodeType() == JsonNodeType.STRING && !jsonNode.asText().equals(expectedNode.getValue());
                    boolean treeFailure = jsonNode.getNodeType() != JsonNodeType.STRING && NODE_COMPARATOR.compare(jsonNode, Http.readTree(expectedNode.getValue())) != 0;
                    if (textFailure || treeFailure) {
                        errors.add("%s have a response body with JSON node %s having value: %s, but was: %s"
                            .formatted(errorPrefix, expectedNode.getKey(), expectedNode.getValue(), Http.writeValueAsString(jsonNode)));
                    }
                }
            }
            if (!errors.isEmpty()) {
                return errors;
            }
        }
        return List.of();
    }
}

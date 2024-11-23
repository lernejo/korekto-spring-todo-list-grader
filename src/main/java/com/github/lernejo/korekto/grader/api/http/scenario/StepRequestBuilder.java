package com.github.lernejo.korekto.grader.api.http.scenario;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StepRequestBuilder {
    private final ScenarioBuilder scenario;
    private final StepBuilder stepBuilder;
    private final String uri;
    private final Verb verb;
    private final String body;
    private final Map<String, String> headers;
    private Collection<ToFormatInput> data;

    StepRequestBuilder(ScenarioBuilder scenario, StepBuilder stepBuilder, String uri, Verb verb, Map<String, String> headers) {
        this(scenario, stepBuilder, uri, verb, headers, null);
    }

    StepRequestBuilder(ScenarioBuilder scenario, StepBuilder stepBuilder, String uri, Verb verb, Map<String, String> headers, String body) {
        this.scenario = scenario;
        this.stepBuilder = stepBuilder;
        this.uri = uri;
        this.verb = verb;
        this.headers = headers;
        this.body = body;
    }

    public StepResponseBuilder to_respond_with_status(int statusCode) {
        StepResponseBuilder response = new StepResponseBuilder(statusCode);
        stepBuilder.setExpectedResponse(response);
        return response;
    }

    public StepRequestBuilder repeat(Set<ToFormatInput> data) {
        this.data = data;
        return this;
    }

    List<StepRequest> build() {
        if (data == null) {
            return List.of(new StepRequest(uri, verb, headers, body));
        } else {
            return data.stream()
                .map(ToFormatInput::toFormatInput)
                .map(i -> new StepRequest(format(uri, i), verb, headers, format(body, i)))
                .toList();
        }
    }

    private String format(String content, Map<String, String> values) {
        String result = content;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            result = result.replace(placeholder, entry.getValue());
        }
        return result;
    }
}

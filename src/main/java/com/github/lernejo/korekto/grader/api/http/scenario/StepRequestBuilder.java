package com.github.lernejo.korekto.grader.api.http.scenario;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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

    public StepResponseMatcher to_respond_with_status(int statusCode) {
        StepResponseMatcher response = new StepResponseMatcher(statusCode);
        stepBuilder.setExpectedResponse(response);
        return response;
    }

    public StepRequestBuilder repeat(Set<ToFormatInput> data) {
        this.data = data;
        return this;
    }

    public StepRequestBuilder with_data(ToFormatInput data) {
        this.data = Set.of(data);
        return this;
    }

    List<StepRequest> build() {
        if (data == null) {
            return List.of(new StepRequest(uri, verb, headers, body, null, String.valueOf(stepBuilder.index)));
        } else {
            AtomicInteger dataIndex = new AtomicInteger();
            return data.stream()
                .map(d -> new Pair<>(d.toFormatInput(), d.name()))
                .map(p -> new StepRequest(format(uri, p.first()), verb, headers, format(body, p.first()), p.second(), stepBuilder.index + "." + dataIndex.incrementAndGet()))
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

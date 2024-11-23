package com.github.lernejo.korekto.grader.api.http.scenario;

import com.github.lernejo.korekto.grader.api.http.Http;
import com.github.lernejo.korekto.grader.api.http.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StepBuilder {
    private final ScenarioBuilder scenario;
    private final Http http;
    StepRequestBuilder request;
    StepResponseBuilder expectedResponse;

    public StepBuilder(ScenarioBuilder scenario, Http http) {
        this.scenario = scenario;
        this.http = http;
    }

    public void setRequest(StepRequestBuilder request) {
        this.request = request;
    }

    public void setExpectedResponse(StepResponseBuilder expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    public List<String> execute() {
        Stream<Pair<StepRequest, Response>> responseStream = request.build()
            .stream()
            .map(req -> new Pair<>(req, req.executeWith(http)));
        if (expectedResponse != null) {
            return responseStream
                .map(r -> expectedResponse.match(r.first(), r.second()))
                .takeWhile(new KeepFirstError())
                .flatMap(List::stream).toList();
        } else {
            return List.of();
        }
    }

    public StepRequestBuilder expect_get(String uri) {
        return expect_get(uri, Map.of());
    }

    public StepRequestBuilder expect_post(String uri, String body) {
        StepRequestBuilder request = new StepRequestBuilder(scenario, this, uri, Verb.POST, Map.of(), body);
        setRequest(request);
        return request;
    }

    public StepRequestBuilder expect_get(String uri, Map<String, String> headers) {
        StepRequestBuilder request = new StepRequestBuilder(scenario, this, uri, Verb.GET, headers);
        setRequest(request);
        return request;
    }

    private static class KeepFirstError implements Predicate<List<String>> {
        private final AtomicBoolean failed = new AtomicBoolean();

        @Override
        public boolean test(List<String> errors) {
            if (failed.get()) {
                return false;
            }
            if (!errors.isEmpty()) {
                failed.set(true);
            }
            return true;
        }
    }
}

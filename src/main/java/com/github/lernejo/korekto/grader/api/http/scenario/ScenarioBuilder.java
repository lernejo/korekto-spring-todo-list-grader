package com.github.lernejo.korekto.grader.api.http.scenario;

import com.github.lernejo.korekto.grader.api.http.Http;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScenarioBuilder {
    private final Http http;
    private final List<StepBuilder> steps = new ArrayList<>();

    public ScenarioBuilder(Http http) {
        this.http = http;
    }

    public List<String> execute() {
        return steps.stream().map(StepBuilder::execute).flatMap(List::stream).toList();
    }

    public ScenarioBuilder new_step(Consumer<StepBuilder> stepBuilderConsumer) {
        StepBuilder stepBuilder = new StepBuilder(this, http, steps.size() +1);
        stepBuilderConsumer.accept(stepBuilder);
        steps.add(stepBuilder);
        return this;
    }
}

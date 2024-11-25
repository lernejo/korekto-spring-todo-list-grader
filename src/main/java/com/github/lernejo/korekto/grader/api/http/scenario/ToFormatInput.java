package com.github.lernejo.korekto.grader.api.http.scenario;

import java.util.Map;

public interface ToFormatInput {

    String name();

    Map<String, String> toFormatInput();
}

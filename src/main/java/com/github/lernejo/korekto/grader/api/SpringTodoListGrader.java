package com.github.lernejo.korekto.grader.api;

import com.github.lernejo.korekto.grader.api.parts.Part1Grader;
import com.github.lernejo.korekto.grader.api.parts.Part2Grader;
import com.github.lernejo.korekto.toolkit.Grader;
import com.github.lernejo.korekto.toolkit.GradingConfiguration;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.Ports;
import com.github.lernejo.korekto.toolkit.partgrader.JacocoCoveragePartGrader;
import com.github.lernejo.korekto.toolkit.partgrader.MavenCompileAndTestPartGrader;
import com.github.lernejo.korekto.toolkit.thirdparty.docker.MappedPortsContainer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SpringTodoListGrader implements Grader<LaunchingContext> {

    private final Logger logger = LoggerFactory.getLogger(SpringTodoListGrader.class);
    private final MappedPortsContainer postgresContainer;

    public SpringTodoListGrader() {
        postgresContainer = new MappedPortsContainer(
            "postgres:16.0-alpine",
            5432,
            (sp, sps) -> "PG up on :" + sp)
            .withEnv("POSTGRES_PASSWORD", "example")
            .startAndWaitForServiceToBeUp();
    }

    @Override
    public void close() {
        postgresContainer.stop();
    }

    @NotNull
    @Override
    public String name() {
        return "Spring todo-list";
    }

    @NotNull
    @Override
    public LaunchingContext gradingContext(@NotNull GradingConfiguration configuration) {
        return new LaunchingContext(configuration, postgresContainer.getServicePort());
    }

    @NotNull
    public Collection<PartGrader<LaunchingContext>> graders() {
        return List.of(
            new MavenCompileAndTestPartGrader<>(
                "Compilation & Tests",
                1.0D),
            new JacocoCoveragePartGrader<>("Code Coverage", 10.0D, 0.90D),
            new Part1Grader("Part 1 - Account", 3.0D),
            new Part2Grader("Part 2 - Todolist creation", 3.0D)
        );
    }

    @NotNull
    @Override
    public String slugToRepoUrl(@NotNull String slug) {
        return "https://github.com/" + slug + "/spring-todo-list";
    }

    @Override
    public boolean needsWorkspaceReset() {
        return true;
    }
}

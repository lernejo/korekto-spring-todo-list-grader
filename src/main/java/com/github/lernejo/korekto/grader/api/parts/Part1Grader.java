package com.github.lernejo.korekto.grader.api.parts;

import com.github.lernejo.korekto.grader.api.LaunchingContext;
import com.github.lernejo.korekto.grader.api.TestData;
import com.github.lernejo.korekto.grader.api.http.Http;
import com.github.lernejo.korekto.grader.api.http.HttpConnectionException;
import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.Ports;
import com.github.lernejo.korekto.toolkit.thirdparty.maven.MavenExecutionHandle;
import com.github.lernejo.korekto.toolkit.thirdparty.maven.MavenExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public record Part1Grader(String name, Double maxGrade) implements PartGrader<LaunchingContext> {

    @NotNull
    private static Map<String, String> basicHeader(TestData.UserDef user) {
        return Map.of("Authorization", Http.basic(user.email(), user.password()));
    }

    @NotNull
    @Override
    public GradePart grade(@NotNull LaunchingContext context) {
        if (context.hasCompilationFailed()) {
            return result(List.of("Not available when there is compilation failures"), 0.0D);
        }
        context.initdb();
        try
            (MavenExecutionHandle ignored = MavenExecutor.executeGoalAsync(context.getExercise(), context.getConfiguration().getWorkspace(),
                "org.springframework.boot:spring-boot-maven-plugin:3.3.5:run " +
                    "-Dspring-boot.run.jvmArguments='" +
                    "-Dserver.port=8085 -Dspring.datasource.url=" + context.pgUrl() + " -Dspring.datasource.username=postgres -Dspring.datasource.password=example" +
                    "'");
             Http client = new Http("http://localhost:8085")) {
            Ports.waitForPortToBeListenedTo(8085, TimeUnit.SECONDS, LaunchingContext.serverStartTime());

            return interrogateServer_part1(client, context);
        } catch (CancellationException e) {
            return result(List.of("Server failed to start within 20 sec."), 0.0D);
        } catch (HttpConnectionException e) {
            return result(List.of("Fail to call server: " + e.getMessage()), 0.0D);
        } catch (RuntimeException e) {
            return result(List.of("Unwanted error during API invocation: " + e.getMessage()), 0.0D);
        } finally {
            Ports.waitForPortToBeFreed(8085, TimeUnit.SECONDS, 5L);
        }
    }

    @NotNull
    private GradePart interrogateServer_part1(Http client, LaunchingContext context) {
        var errors = client.new_scenario()
            .new_step(s ->
                s.expect_get("/api/account")
                    .to_respond_with_status(401)
            )
            .new_step(s ->
                s.expect_post("/api/account", """
                        {
                            "email": "{email}",
                            "password": "{password}"
                        }
                        """)
                    .repeat(Set.of(context.testData.user1, context.testData.user2, context.testData.user3, context.testData.user4))
                    .to_respond_with_status(201)
            )
            .new_step(s ->
                s.expect_get("/api/account/self", basicHeader(context.testData.user2))
                    .to_respond_with_status(200)
                    .and_json_body_containing_node_keys("uuid", "email", "created_at")
                    .and_json_body_containing_nodes(Map.of("email", context.testData.user2.email()))
            )
            .new_step(s ->
                s.expect_post("/api/account", """
                        {
                            "email": "{email}",
                            "password": "{password}"
                        }
                        """)
                    .with_data(context.testData.user3)
                    .to_respond_with_status(409)
            )
            .execute();

        return result(errors, maxGrade - errors.size() * (maxGrade / 3));
    }
}

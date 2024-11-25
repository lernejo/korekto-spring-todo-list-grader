package com.github.lernejo.korekto.grader.api.parts;

import com.github.lernejo.korekto.grader.api.LaunchingContext;
import com.github.lernejo.korekto.grader.api.http.Http;
import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.lernejo.korekto.grader.api.LaunchingContext.basicHeader;

public record Part2Grader(String name, Double maxGrade) implements PartGrader<LaunchingContext> {

    static void interrogateServer_part2(Http client, LaunchingContext context) {
        context.part2Errors = client.new_scenario()
            .new_step(s ->
                s.expect_get("/api/todolist")
                    .to_respond_with_status(401)
            )
            .new_step(s ->
                s.expect_get("/api/todolist", basicHeader(context.testData.user1))
                    .to_respond_with_status(200)
                    .and_json_body_containing_node_keys("items", "count", "page", "page_size", "total_count", "total_page_count")
                    .and_json_body_containing_nodes(Map.of("count", "0", "page", "1", "page_size", "25", "total_count", "0"))
            )
            .new_step(s ->
                s.expect_post("/api/todolist", "{toto", basicHeader(context.testData.user1))
                    .to_respond_with_status(400)
            )
            .new_step(s ->
                s.expect_post("/api/todolist", """
                        {
                            "title": "{title}",
                            "description": "{description}"
                        }
                        """, basicHeader(context.testData.user1))
                    .repeat(Set.of(context.testData.todoList1, context.testData.todoList2))
                    .to_respond_with_status(201)
            )
            .new_step(s ->
                s.expect_post("/api/todolist", """
                        {
                            "title": "{title}",
                            "description": "{password}"
                        }
                        """, basicHeader(context.testData.user2))
                    .with_data(context.testData.todoList3)
                    .to_respond_with_status(201)
            )
            .new_step(s ->
                s.expect_post("/api/todolist", """
                        {
                            "title": "{title}",
                            "description": "{password}"
                        }
                        """, basicHeader(context.testData.user2))
                    .with_data(context.testData.todoList1)
                    .to_respond_with_status(409)
            )
            .execute();
    }

    @NotNull
    @Override
    public GradePart grade(@NotNull LaunchingContext context) {
        if (context.hasCompilationFailed()) {
            return result(List.of("Not available when there are compilation failures"), 0.0D);
        }
        if (context.part2Errors == null) {
            return result(List.of("Not available when there are Part 1 errors"), 0.0D);
        }
        return result(context.part2Errors, maxGrade - context.part2Errors.size() * (maxGrade / 3));
    }
}
